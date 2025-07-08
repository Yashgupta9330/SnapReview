package com.bookreview.controller;

import com.bookreview.entity.Book;
import com.bookreview.entity.BookStatus;
import com.bookreview.entity.User;
import com.bookreview.entity.Genre;
import com.bookreview.repository.UserRepository;
import com.bookreview.repository.GenreRepository;
import com.bookreview.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;

    @GetMapping
    public List<Book> getBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookService.getBook(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable Long genreId) {
        Genre genre = genreRepository.findById(genreId).orElse(null);
        if (genre == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(bookService.getBooksByGenre(genre));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {
        User author = userRepository.findById(authorId).orElse(null);
        if (author == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(bookService.getBooksByAuthor(author));
    }

    @GetMapping("/status/{status}")
    public List<Book> getBooksByStatus(@PathVariable BookStatus status) {
        return bookService.getBooksByStatus(status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, book));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paged")
    public Page<Book> getBooksPaged(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookService.getAllBooks(pageable);
    }
}