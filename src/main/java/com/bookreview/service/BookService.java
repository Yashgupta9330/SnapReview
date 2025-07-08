package com.bookreview.service;

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

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Optional<Book> getBook(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> getBooksByStatus(BookStatus status) {
        return bookRepository.findByStatus(status);
    }

    public List<Book> getBooksByAuthor(User author) {
        return bookRepository.findByAuthor(author);
    }

    public List<Book> getBooksByGenre(Genre genre) {
        return bookRepository.findByGenresContaining(genre);
    }

    public Book updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setSubtitle(updatedBook.getSubtitle());
            book.setDescription(updatedBook.getDescription());
            book.setAuthor(updatedBook.getAuthor());
            book.setGenres(updatedBook.getGenres());
            book.setStatus(updatedBook.getStatus());
            // ... update other fields as needed
            return bookRepository.save(book);
        }).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
}