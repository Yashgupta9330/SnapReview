# Entity Relationship Diagram (ERD)

```mermaid
erDiagram
  USER {
    Long id PK
    String username UNIQUE
    String email UNIQUE
    String password
    String firstName
    String lastName
    String biography
    LocalDate birthDate
    String nationality
    String website
    String penName
    String profileImageUrl
    Boolean isActive
    Boolean isVerified
    Boolean isLocked
    Integer failedLoginAttempts
    LocalDateTime lastLoginAt
    LocalDateTime passwordChangedAt
    String roles
    String createdBy
    String lastModifiedBy
    LocalDateTime createdAt
    LocalDateTime updatedAt
  }
  BOOK {
    Long id PK
    String title
    String subtitle
    String isbn UNIQUE
    String isbn13
    String description
    LocalDate publicationDate
    Integer pageCount
    String language
    String publisher
    String coverImageUrl
    Decimal price
    String currency
    String searchVector
    String status
    Double averageRating
    Integer reviewCount
    Long totalRatingSum
    Long viewCount
    Boolean isActive
    Boolean isFeatured
    Boolean isBestseller
    Boolean isFree
    String createdBy
    String lastModifiedBy
    LocalDateTime createdAt
    LocalDateTime updatedAt
  }
  GENRE {
    Long id PK
    String name UNIQUE
    String slug UNIQUE
    String description
    Boolean isActive
    LocalDateTime createdAt
    LocalDateTime updatedAt
  }
  REVIEW {
    Long id PK
    String content
    Integer rating
    String title
    Integer helpfulCount
    Integer notHelpfulCount
    Boolean isActive
    Boolean isFeatured
    LocalDateTime createdAt
    LocalDateTime updatedAt
  }
  REVIEWHELPFULNESS {
    Long id PK
    Boolean isHelpful
    LocalDateTime createdAt
  }
  REFRESHTOKEN {
    Long id PK
    String token UNIQUE
    LocalDateTime expiryDate
    Boolean isRevoked
    LocalDateTime createdAt
  }

  USER ||--o{ BOOK : author
  USER ||--o{ BOOK : coauthor
  USER ||--o{ REVIEW : writes
  USER ||--o{ REFRESHTOKEN : has
  USER ||--o{ REVIEWHELPFULNESS : votes
  BOOK ||--o{ REVIEW : has
  BOOK ||--o{ GENRE : categorized_as
  REVIEW ||--o{ REVIEWHELPFULNESS : has
  GENRE ||--o{ BOOK : includes

  %% Join tables (many-to-many)
  BOOK ||--o{ BOOK_CO_AUTHORS : coauthor_link
  BOOK_CO_AUTHORS {
    Long book_id FK
    Long author_id FK
  }
  BOOK ||--o{ BOOK_GENRES : genre_link
  BOOK_GENRES {
    Long book_id FK
    Long genre_id FK
  }
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