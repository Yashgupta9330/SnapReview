# Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    USER ||--o{ BOOK : writes
    USER {
        Long id PK
        string username
        string email
        string password
        string firstName
        string lastName
        string biography
        LocalDate birthDate
        string nationality
        string website
        string penName
        string profileImageUrl
        boolean isActive
        boolean isVerified
        boolean isLocked
        int failedLoginAttempts
        LocalDateTime lastLoginAt
        LocalDateTime passwordChangedAt
        string roles
        string createdBy
        string lastModifiedBy
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
    
    BOOK ||--o{ REVIEW : has
    BOOK {
        Long id PK
        string title
        string subtitle
        string isbn
        string isbn13
        string description
        LocalDate publicationDate
        int pageCount
        string language
        string publisher
        string coverImageUrl
        decimal price
        string currency
        string searchVector
        string status
        double averageRating
        int reviewCount
        Long totalRatingSum
        Long viewCount
        boolean isActive
        boolean isFeatured
        boolean isBestseller
        boolean isFree
        string createdBy
        string lastModifiedBy
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
    
    GENRE ||--o{ BOOK_GENRES : used_in
    GENRE {
        Long id PK
        string name
        string slug
        string description
        boolean isActive
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
    
    REVIEW ||--o{ REVIEWHELPFULNESS : has
    REVIEW {
        Long id PK
        string content
        int rating
        string title
        int helpfulCount
        int notHelpfulCount
        boolean isActive
        boolean isFeatured
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
    
    REVIEWHELPFULNESS {
        Long id PK
        boolean isHelpful
        LocalDateTime createdAt
    }
    
    REFRESHTOKEN {
        Long id PK
        string token
        LocalDateTime expiryDate
        boolean isRevoked
        LocalDateTime createdAt
    }
    
    BOOK_CO_AUTHORS {
        Long book_id FK
        Long author_id FK
    }
    
    BOOK_GENRES {
        Long book_id FK
        Long genre_id FK
    }
    
    USER ||--o{ REVIEW : writes
    USER ||--o{ REFRESHTOKEN : owns
    USER ||--o{ REVIEWHELPFULNESS : votes
    BOOK ||--o{ REVIEWHELPFULNESS : referenced_in
    BOOK ||--o{ BOOK_GENRES : genre_link
    BOOK ||--o{ BOOK_CO_AUTHORS : coauthored_by
```


# ER Diagram Documentation (Entities & Relationships)

## 1. User
**Fields:** id, username, email, password, roles, etc.

**Relationships:**
- @OneToMany authored books
- @ManyToMany co-authored books
- @OneToMany reviews
- @OneToMany helpfulness votes
- @OneToMany refresh tokens

## 2. Book
**Fields:** id, title, isbn, description, status, averageRating, etc.

**Relationships:**
- @ManyToOne author (User)
- @ManyToMany co-authors (User)
- @ManyToMany genres
- @OneToMany reviews

## 3. Review
**Fields:** id, content, rating, isActive, isFeatured, etc.

**Relationships:**
- @ManyToOne user
- @ManyToOne book
- @OneToMany helpfulness votes

## 4. ReviewHelpfulness
**Fields:** id, isHelpful

**Relationships:**
- @ManyToOne user
- @ManyToOne review

## 5. Genre
**Fields:** id, name, slug, description

**Relationships:**
- @ManyToMany books

## 6. RefreshToken
**Fields:** id, token, expiryDate, isRevoked

**Relationships:**
- @ManyToOne user

---

## 📌 Index Choices

### ✅ Why Use Indexes?
Indexes are added to improve performance for filtering, searching, sorting, and joining large datasets.

### ✅ Indexes Used
| Table              | Indexed Fields                                      | Purpose                                 |
|--------------------|----------------------------------------------------|-----------------------------------------|
| books              | title, isbn, author_id, average_rating, publication_date, status, search_vector | Fast search, filtering, and sort        |
| reviews            | user_id, book_id, rating, created_at                | Efficient fetch by user/book            |
| genres             | name, slug                                         | Fast lookup of genres                   |
| review_helpfulness | user_id, review_id                                 | Quick lookups to avoid duplicate votes  |
| refresh_tokens     | token, user_id, expiry_date                        | Token validation and expiry handling    |

### ✅ Unique Constraints
- `isbn` in Book
- `user_id + book_id` in Review (prevents duplicate reviews)
- `user_id + review_id` in ReviewHelpfulness (prevents duplicate votes)

---

## ⚙️ Fetch Strategies
| Relationship                                 | Strategy | Reason                                      |
|----------------------------------------------|----------|---------------------------------------------|
| @ManyToOne (User → Book, User → Review)      | LAZY     | Avoid loading user details unless required  |
| @OneToMany (Book → Reviews, User → Reviews)  | LAZY     | Reviews are often not needed immediately    |
| @ManyToMany (Book → Genres, Book → CoAuthors)| LAZY     | Keep memory and join queries optimized      |
| @OneToMany (Review → Helpfulness)            | LAZY     | Only fetch votes when displaying them       |

### 🔍 Why mostly LAZY?
To reduce memory consumption and prevent unnecessary JOINs. You can use `@EntityGraph` or manual `.fetchJoin()` when needed eagerly.

---

## 🔄 Cascade Behavior
| Relationship                                              | Cascade         | Reason                                         |
|----------------------------------------------------------|-----------------|------------------------------------------------|
| @OneToMany(mappedBy="book", cascade = ALL) → Reviews    | All             | Reviews should be created/deleted with book    |
| @OneToMany(mappedBy="review", cascade = ALL) → Helpfulness | All         | Voting is embedded with review lifecycle       |
| @ManyToMany(fetch = LAZY, cascade = {PERSIST, MERGE}) → Genres | Persist/Merge | Don't delete genres if a book is deleted   |
| @ManyToOne                                              | No Cascade      | User should remain even if book/review is deleted |

---

## ✅ Summary
- Indexes are strategically applied to fields queried or joined frequently.
- Lazy fetch strategy avoids performance issues and reduces unnecessary DB access.
- Cascade operations are added where parent-child dependency is strong (e.g. book → review). 