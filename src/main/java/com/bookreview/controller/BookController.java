package com.bookreview.controller;

import com.bookreview.dto.BookDTO;
import com.bookreview.entity.Book;
import com.bookreview.entity.BookStatus;
import com.bookreview.entity.Genre;
import com.bookreview.entity.User;
import com.bookreview.repository.GenreRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody Book book, Principal principal) {
        // Set the author to the currently authenticated user
        String username = principal.getName();
        User author = userRepository.findByUsername(username).orElseThrow();
        book.setAuthor(author);
        // Replace genres with managed entities
        Set<Genre> managedGenres = new HashSet<>();
        for (Genre genre : book.getGenres()) {
            Genre managedGenre = genreRepository.findById(genre.getId())
                .orElseThrow(() -> new IllegalArgumentException("Genre not found: " + genre.getId()));
            managedGenres.add(managedGenre);
        }
        book.setGenres(managedGenres);
        BookDTO saved = bookService.saveBook(book);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        return bookService.getBook(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<BookDTO>> getBooksByGenre(@PathVariable Long genreId) {
        Optional<Genre> genre = genreRepository.findById(genreId);
        return genre.map(g -> ResponseEntity.ok(bookService.getBooksByGenre(g)))
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BookDTO>> getBooksByAuthor(@PathVariable Long authorId) {
        Optional<User> author = userRepository.findById(authorId);
        return author.map(a -> ResponseEntity.ok(bookService.getBooksByAuthor(a)))
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookDTO>> getBooksByStatus(@PathVariable BookStatus status) {
        return ResponseEntity.ok(bookService.getBooksByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        return bookService.updateBook(id, book)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<BookDTO>> getBooksPaged(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> searchBooks(@RequestParam(required = false) String title,
                                                     @RequestParam(required = false) String author,
                                                     @RequestParam(required = false) String genre,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.searchBooks(title, author, genre, pageable));
    }
}
