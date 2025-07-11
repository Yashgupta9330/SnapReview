package com.bookreview.controller;

import com.bookreview.dto.BookDTO;
import com.bookreview.dto.BookCreateRequest;
import com.bookreview.entity.Book;
import com.bookreview.entity.BookStatus;
import com.bookreview.entity.Genre;
import com.bookreview.entity.User;
import com.bookreview.repository.GenreRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.repository.BookRepository;
import com.bookreview.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookCreateRequest request, Principal principal) {
        try {
            User author = getCurrentUser(principal);
            BookDTO createdBook = bookService.createBook(request, author);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (Exception e) {
            logger.error("Error creating book: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable Long id) {
        Optional<BookDTO> bookOpt = bookService.getBook(id);
        if (bookOpt.isPresent()) {
            return ResponseEntity.ok(bookOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Book not found"));
        }
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<BookDTO>> getBooksByGenre(@PathVariable Long genreId) {
        Genre genre = genreRepository.findById(genreId)
            .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + genreId));
        
        List<BookDTO> books = bookService.getBooksByGenre(genre);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BookDTO>> getBooksByAuthor(@PathVariable Long authorId) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("Author not found: " + authorId));
        
        List<BookDTO> books = bookService.getBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookDTO>> getBooksByStatus(@PathVariable BookStatus status) {
        List<BookDTO> books = bookService.getBooksByStatus(status);
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO, Principal principal) {
        try {
            // Check if book exists
            Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
            
            // Check permissions
            User currentUser = getCurrentUser(principal);
            if (!bookService.canUserModifyBook(book, currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to update this book"));
            }

            // Update book
            BookDTO updatedBook = bookService.updateBook(id, bookDTO)
                .orElseThrow(() -> new EntityNotFoundException("Book not found after update: " + id));
            
            return ResponseEntity.ok(updatedBook);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating book {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update book"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id, Principal principal) {
        try {
            // Check if book exists
            Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
            
            // Check permissions
            User currentUser = getCurrentUser(principal);
            if (!bookService.canUserModifyBook(book, currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to delete this book"));
            }

            // Delete book
            bookService.deleteBook(id, currentUser);
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting book {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete book"));
        }
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<BookDTO>> getBooksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BookDTO> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BookDTO> books = bookService.searchBooks(title, author, genre, pageable);
        return ResponseEntity.ok(books);
    }

    private User getCurrentUser(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }
}