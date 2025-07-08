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

## Design Choices

### Indexes & Constraints
- PK = Primary Key, FK = Foreign Key, UNIQUE = Unique constraint
- Unique constraints on username, email, book ISBN, genre name/slug, review (user+book)
- Indexes for search and performance (e.g., search_vector, status, genre)

### Fetch Strategies
- LAZY for collections and most relationships
- EAGER for roles (security)

### Cascades
- CascadeType.ALL for child collections (reviews, refresh tokens, helpfulness)
- No cascade on user deletion to preserve review history

### Audit Fields
- All main entities have createdAt, updatedAt, and audit fields for tracking changes 