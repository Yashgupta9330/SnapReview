package com.bookreview.service;

import com.bookreview.dto.BookDTO;
import com.bookreview.entity.Book;
import com.bookreview.entity.BookStatus;
import com.bookreview.entity.User;
import com.bookreview.entity.Genre;
import com.bookreview.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    private BookDTO toDTO(Book book) {
        return new BookDTO(
            book.getId(),
            book.getTitle(),
            book.getSubtitle(),
            book.getIsbn(),
            book.getPublicationDate(),
            book.getAverageRating(),
            book.getAuthor() != null ? book.getAuthor().getUsername() : null,
            book.getGenres() != null ? book.getGenres().stream().map(g -> g.getName()).collect(Collectors.toList()) : null
        );
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BookDTO saveBook(Book book) {
        return toDTO(bookRepository.save(book));
    }

    public Optional<BookDTO> getBook(Long id) {
        return bookRepository.findById(id).map(this::toDTO);
    }

    public List<BookDTO> getBooksByStatus(BookStatus status) {
        return bookRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByAuthor(User author) {
        return bookRepository.findByAuthor(author).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByGenre(Genre genre) {
        return bookRepository.findByGenresContaining(genre).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<BookDTO> updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id).map(book -> {
            if (updatedBook.getTitle() != null) book.setTitle(updatedBook.getTitle());
            if (updatedBook.getSubtitle() != null) book.setSubtitle(updatedBook.getSubtitle());
            if (updatedBook.getDescription() != null) book.setDescription(updatedBook.getDescription());
            if (updatedBook.getGenres() != null) book.setGenres(updatedBook.getGenres());
            if (updatedBook.getStatus() != null) book.setStatus(updatedBook.getStatus());
            if (updatedBook.getAuthor() != null) book.setAuthor(updatedBook.getAuthor());
            return toDTO(bookRepository.save(book));
        });
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<BookDTO> searchBooks(String title, String author, String genre, Pageable pageable) {
        // This is a placeholder implementation. Replace with actual search logic as needed.
        return bookRepository.findAll(pageable).map(this::toDTO);
    }
}