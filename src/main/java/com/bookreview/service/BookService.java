package com.bookreview.service;

import com.bookreview.dto.BookDTO;
import com.bookreview.dto.BookCreateRequest;
import com.bookreview.entity.Book;
import com.bookreview.entity.BookStatus;
import com.bookreview.entity.User;
import com.bookreview.entity.Genre;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.GenreRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.exception.BookAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;

    /**
     * Convert Book entity to BookDTO
     */
    public BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }

        return BookDTO.builder()
            .id(book.getId())
            .title(book.getTitle())
            .subtitle(book.getSubtitle())
            .isbn(book.getIsbn())
            .publicationDate(book.getPublicationDate())
            .averageRating(book.getAverageRating())
            .description(book.getDescription())
            .status(book.getStatus())
            .publisher(book.getPublisher())
            .authorUsername(book.getAuthor() != null ? book.getAuthor().getUsername() : null)
            .genreNames(book.getGenres() != null ? 
                book.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.toList()) : 
                Collections.emptyList())
            .coAuthorUsernames(book.getCoAuthors() != null ? 
                book.getCoAuthors().stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList()) : 
                Collections.emptyList())
            .build();
    }

    /**
     * Convert BookCreateRequest to Book entity
     */
    public Book fromCreateRequest(BookCreateRequest request, User author) {
        if (request == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setSubtitle(request.getSubtitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationDate(request.getPublicationDate());
        book.setDescription(request.getDescription());
        book.setAuthor(author);
        
        if (request.getStatus() != null) {
            book.setStatus(BookStatus.valueOf(request.getStatus()));
        }

        // Set genres if provided
        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            Set<Genre> genres = request.getGenreIds().stream()
                .map(id -> genreRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + id)))
                .collect(Collectors.toSet());
            book.setGenres(genres);
        }

        // Set co-authors if provided
        if (request.getCoAuthorIds() != null && !request.getCoAuthorIds().isEmpty()) {
            Set<User> coAuthors = request.getCoAuthorIds().stream()
                .map(id -> userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + id)))
                .collect(Collectors.toSet());
            book.setCoAuthors(coAuthors);
        }

        return book;
    }

    /**
     * Apply partial updates from BookDTO to existing Book entity
     */
    public void updateBookFromDTO(Book book, BookDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getSubtitle() != null) {
            book.setSubtitle(dto.getSubtitle());
        }
        if (dto.getDescription() != null) {
            book.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            book.setStatus(dto.getStatus());
        }
        if (dto.getPublisher() != null) {
            book.setPublisher(dto.getPublisher());
        }
        if (dto.getIsbn() != null) {
            book.setIsbn(dto.getIsbn());
        }
        if (dto.getPublicationDate() != null) {
            book.setPublicationDate(dto.getPublicationDate());
        }
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public BookDTO createBook(BookCreateRequest request, User author) {
        // Check if book already exists
        if (bookRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new BookAlreadyExistsException("Book with title '" + request.getTitle() + "' already exists");
        }

        Book book = fromCreateRequest(request, author);
        Book savedBook = bookRepository.save(book);
        return toDTO(savedBook);
    }

    public Optional<BookDTO> getBook(Long id) {
        return bookRepository.findById(id).map(this::toDTO);
    }

    public List<BookDTO> getBooksByStatus(BookStatus status) {
        return bookRepository.findByStatus(status).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByAuthor(User author) {
        return bookRepository.findByAuthor(author).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByGenre(Genre genre) {
        return bookRepository.findByGenresContaining(genre).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public Optional<BookDTO> updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id).map(book -> {
            updateBookFromDTO(book, bookDTO);
            return toDTO(bookRepository.save(book));
        });
    }

    public void deleteBook(Long id, User currentUser) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
        
        if (!canUserModifyBook(book, currentUser)) {
            throw new AccessDeniedException("You are not allowed to delete this book.");
        }
        
        bookRepository.deleteById(id);
    }

    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<BookDTO> searchBooks(String title, String author, String genre, Pageable pageable) {
        // TODO: Implement actual search logic based on criteria
        return bookRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Check if user can modify (update/delete) the book
     */
    public boolean canUserModifyBook(Book book, User user) {
        if (book == null || user == null) {
            return false;
        }

        // Check if user is the author
        boolean isAuthor = book.getAuthor() != null && 
            book.getAuthor().getId().equals(user.getId());

        // Check if user is a co-author
        boolean isCoAuthor = book.getCoAuthors() != null && 
            book.getCoAuthors().stream()
                .anyMatch(coAuthor -> coAuthor.getId().equals(user.getId()));

        // Check if user is moderator or admin
        boolean isModerator = user.getRoles() != null && 
            user.getRoles().stream()
                .anyMatch(role -> "MODERATOR".equals(role) || "ADMIN".equals(role));

        return isAuthor || isCoAuthor || isModerator;
    }
}