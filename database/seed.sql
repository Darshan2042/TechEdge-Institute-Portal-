USE techedge;

-- =========================================================
-- USERS
-- Password for all seeded users: Password@123
-- =========================================================

INSERT INTO users
(full_name, email, password_hash, phone, role, is_active)
VALUES
    ('Anjali Deshmukh',
     'admin@techedge.in',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011001',
     'ADMIN',
     TRUE),

    ('Rohit Patil',
     'rohit.patil@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011002',
     'STUDENT',
     TRUE),

    ('Sneha Kulkarni',
     'sneha.k@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011003',
     'STUDENT',
     TRUE),

    ('Amit Joshi',
     'amit.joshi@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011004',
     'STUDENT',
     TRUE),

    ('Priya Shah',
     'priya.shah@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011005',
     'STUDENT',
     TRUE),

    ('Vikas More',
     'vikas.more@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011006',
     'STUDENT',
     TRUE),

    ('Neha Pawar',
     'neha.pawar@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011007',
     'STUDENT',
     TRUE),

    ('Kunal Jadhav',
     'kunal.jadhav@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011008',
     'STUDENT',
     TRUE),

    ('Pooja Patil',
     'pooja.patil@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011009',
     'STUDENT',
     TRUE),

    ('Akash Deshmukh',
     'akash.deshmukh@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011010',
     'STUDENT',
     TRUE),

    ('Riya Kulkarni',
     'riya.kulkarni@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011011',
     'STUDENT',
     TRUE),

    ('Sagar Shinde',
     'sagar.shinde@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '9822011012',
     'STUDENT',
     FALSE);


-- =========================================================
-- STUDENTS
-- =========================================================

INSERT INTO students
(user_id, qualification, graduation_year, city, resume_url)
VALUES
    (2, 'B.E. Computer Engineering', 2026, 'Pune',
     'https://example.com/resumes/rohit-patil.pdf'),

    (3, 'B.E. Computer Engineering', 2026, 'Pune',
     'https://example.com/resumes/sneha-kulkarni.pdf'),

    (4, 'B.E. Information Technology', 2026, 'Mumbai',
     'https://example.com/resumes/amit-joshi.pdf'),

    (5, 'B.E. Computer Engineering', 2025, 'Nashik',
     'https://example.com/resumes/priya-shah.pdf'),

    (6, 'B.E. Computer Engineering', 2026, 'Pune',
     'https://example.com/resumes/vikas-more.pdf'),

    (7, 'B.E. Electronics Engineering', 2026, 'Aurangabad',
     'https://example.com/resumes/neha-pawar.pdf'),

    (8, 'B.E. Computer Engineering', 2025, 'Pune',
     'https://example.com/resumes/kunal-jadhav.pdf'),

    (9, 'B.E. Information Technology', 2026, 'Nashik',
     'https://example.com/resumes/pooja-patil.pdf'),

    (10, 'B.E. Computer Engineering', 2026, 'Dhule',
     'https://example.com/resumes/akash-deshmukh.pdf'),

    (11, 'B.E. Computer Engineering', NULL, 'Pune',
     'https://example.com/resumes/riya-kulkarni.pdf'),

    (12, 'B.E. Information Technology', 2025, 'Mumbai',
     'https://example.com/resumes/sagar-shinde.pdf');



-- =========================================================
-- COURSES
-- =========================================================

INSERT INTO courses
(code, title, description, category, duration_weeks, fee, level, is_active)
VALUES

    ('JAVA-FS',
     'Java Full Stack Development',
     'Core Java, Spring Boot, REST APIs and JPA with a capstone project.',
     'TECHNICAL',
     16,
     45000.00,
     'INTERMEDIATE',
     TRUE),

    ('SQL-DB',
     'MySQL Database Essentials',
     'Schema design, joins, indexing, stored procedures and query tuning.',
     'TECHNICAL',
     6,
     15000.00,
     'BEGINNER',
     TRUE),

    ('WEB-FE',
     'Web Development with HTML, CSS & JavaScript',
     'Semantic markup, responsive layout, DOM and fetch-based API calls.',
     'TECHNICAL',
     8,
     20000.00,
     'BEGINNER',
     TRUE),

    ('APTI-01',
     'Quantitative Aptitude & Logical Reasoning',
     'Arithmetic, data interpretation and puzzles for placement tests.',
     'APTITUDE',
     4,
     8000.00,
     'BEGINNER',
     TRUE),

    ('COMM-01',
     'Communication & Interview Skills',
     'Group discussion, resume writing and mock interviews.',
     'COMMUNICATION',
     4,
     7000.00,
     'BEGINNER',
     TRUE),

    ('SPRING-01',
     'Spring Boot Backend Development',
     'Spring Boot, REST APIs, Spring Data JPA, Spring Security and backend development.',
     'TECHNICAL',
     10,
     28000.00,
     'INTERMEDIATE',
     TRUE),

    ('PY-DS',
     'Python Data Science Fundamentals',
     'Python programming, NumPy, Pandas, data visualization and basic machine learning.',
     'TECHNICAL',
     12,
     30000.00,
     'INTERMEDIATE',
     FALSE),

    ('VERBAL-01',
     'Verbal Ability & English',
     'Grammar, vocabulary, reading comprehension and verbal reasoning for placements.',
     'APTITUDE',
     4,
     7500.00,
     'BEGINNER',
     TRUE);


-- =========================================================
-- TOPICS
-- Deliberately inserted out of sort_order for testing
-- =========================================================

INSERT INTO topics
(course_id, title, sort_order)
VALUES

-- JAVA-FS
(1, 'Collections & Streams', 2),
(1, 'Spring Boot & REST APIs', 4),
(1, 'Core Java & OOP', 1),
(1, 'JPA & Hibernate', 5),
(1, 'Exception Handling & Multithreading', 3),

-- SQL-DB
(2, 'Indexing & Query Optimization', 4),
(2, 'SQL Basics & DDL', 1),
(2, 'Joins & Subqueries', 2),
(2, 'Stored Procedures & Views', 5),
(2, 'Transactions & Constraints', 3),

-- WEB-FE
(3, 'JavaScript & DOM', 3),
(3, 'HTML Fundamentals', 1),
(3, 'Fetch API & REST Integration', 5),
(3, 'CSS & Responsive Design', 2),
(3, 'Forms & Browser Validation', 4),

-- APTI-01
(4, 'Data Interpretation', 4),
(4, 'Number System', 1),
(4, 'Logical Reasoning', 3),
(4, 'Percentages & Profit Loss', 2),
(4, 'Puzzles & Seating Arrangement', 5),

-- COMM-01
(5, 'Resume Writing', 2),
(5, 'Mock Interviews', 5),
(5, 'Group Discussion', 3),
(5, 'Professional Communication', 4),
(5, 'Communication Fundamentals', 1),

-- SPRING-01
(6, 'Spring Boot Fundamentals', 1),
(6, 'Spring Data JPA', 3),
(6, 'Spring Security & JWT', 5),
(6, 'REST API Development', 2),
(6, 'Exception Handling & Validation', 4),

-- PY-DS
(7, 'Pandas', 2),
(7, 'Python Fundamentals', 1),
(7, 'Data Visualization', 4),
(7, 'NumPy', 3),
(7, 'Machine Learning Basics', 5),

-- VERBAL-01
(8, 'Reading Comprehension', 3),
(8, 'Vocabulary', 2),
(8, 'Grammar', 1),
(8, 'Sentence Correction', 4),
(8, 'Verbal Reasoning', 5);



-- =========================================================
-- BATCHES
-- =========================================================

INSERT INTO batches
(course_id, batch_code, start_date, end_date, timing, mode,
 trainer_name, total_seats, available_seats, status)
VALUES

    (1, 'JAVA-FS-2026-A',
     '2026-10-05', '2027-01-25',
     '09:00-11:00', 'OFFLINE',
     'Mahesh Jadhav',
     30, 12, 'UPCOMING'),

    (1, 'JAVA-FS-2026-B',
     '2026-10-12', '2027-02-01',
     '18:30-20:30', 'ONLINE',
     'Priya Nair',
     25, 0, 'UPCOMING'),

    (2, 'SQL-DB-2026-C',
     '2026-09-28', '2026-11-09',
     '11:00-12:30', 'HYBRID',
     'Mahesh Jadhav',
     40, 22, 'ONGOING'),

    (3, 'WEB-FE-2026-D',
     '2026-09-27', '2026-11-20',
     '10:00-12:00', 'ONLINE',
     'Riya Mehta',
     35, 35, 'UPCOMING'),

    (4, 'APTI-01-2026-E',
     '2026-10-01', '2026-10-28',
     '14:00-16:00', 'OFFLINE',
     'Amit Kulkarni',
     50, 31, 'UPCOMING'),

    (5, 'COMM-01-2026-F',
     '2026-10-03', '2026-10-31',
     '16:00-18:00', 'HYBRID',
     'Sneha Joshi',
     30, 18, 'UPCOMING'),

    (6, 'SPRING-01-2026-G',
     '2026-11-01', '2027-01-10',
     '09:00-11:00', 'ONLINE',
     'Rahul Nair',
     30, 20, 'UPCOMING'),

    (7, 'PY-DS-2026-H',
     '2026-05-01', '2026-07-31',
     '10:00-12:00', 'OFFLINE',
     'Kiran Shah',
     25, 5, 'COMPLETED'),

    (8, 'VERBAL-01-2026-I',
     '2026-10-15', '2026-11-12',
     '17:00-18:30', 'ONLINE',
     'Neha Patil',
     40, 32, 'UPCOMING'),

    (1, 'JAVA-FS-2026-J',
     '2026-11-15', '2027-03-01',
     '08:00-10:00', 'HYBRID',
     'Mahesh Jadhav',
     30, 24, 'UPCOMING'),

    (2, 'SQL-DB-2026-K',
     '2026-12-01', '2027-01-15',
     '18:00-19:30', 'ONLINE',
     'Priya Nair',
     35, 28, 'UPCOMING'),

    (6, 'SPRING-01-2026-L',
     '2026-12-10', '2027-02-20',
     '19:00-21:00', 'OFFLINE',
     'Rahul Nair',
     25, 17, 'UPCOMING');



-- =========================================================
-- OFFERS
-- =========================================================

INSERT INTO offers
(title, description, discount_percent, coupon_code,
 valid_from, valid_to, is_active)
VALUES

    ('Diwali Special',
     'Festive discount on all technical courses.',
     20.00,
     'DIWALI20',
     '2026-10-01',
     '2026-11-15',
     TRUE),

    ('Early Bird',
     'Enrol 30 days before the batch starts.',
     10.00,
     'EARLY10',
     '2026-09-01',
     '2026-12-31',
     TRUE),

    ('Summer Camp',
     'Expired offer retained for history.',
     25.00,
     'SUMMER25',
     '2026-04-01',
     '2026-06-30',
     TRUE),

    ('Placement Booster',
     'Special discount for placement preparation courses.',
     15.00,
     'PLACE15',
     '2026-09-15',
     '2026-10-15',
     TRUE);


-- =========================================================
-- COURSE OFFERS
-- =========================================================

INSERT INTO course_offers
(course_id, offer_id)
VALUES

    (1, 1),
    (2, 1),
    (3, 1),

    (1, 2),
    (4, 2),

    (5, 3),

    (6, 4);


-- =========================================================
-- ENROLLMENTS
-- 18 rows covering all 6 statuses
-- =========================================================

INSERT INTO enrollments
(student_id, batch_id, status, applied_at, decided_at,
 fee_paid, payment_ref, remarks)
VALUES

    (1, 1, 'APPLIED',
     '2026-09-20 10:00:00', NULL,
     0.00, NULL, 'Application submitted'),

    (2, 1, 'APPROVED',
     '2026-09-18 11:00:00', '2026-09-19 10:00:00',
     45000.00, 'PAY-1001', 'Approved by admin'),

    (3, 2, 'REJECTED',
     '2026-09-17 09:30:00', '2026-09-18 12:00:00',
     0.00, NULL, 'Eligibility criteria not met'),

    (4, 3, 'ACTIVE',
     '2026-09-10 14:00:00', '2026-09-11 10:00:00',
     15000.00, 'PAY-1002', 'Currently attending'),

    (5, 3, 'COMPLETED',
     '2026-08-20 10:00:00', '2026-08-21 09:00:00',
     15000.00, 'PAY-1003', 'Course completed'),

    (6, 4, 'DROPPED',
     '2026-08-25 12:00:00', '2026-08-26 11:00:00',
     20000.00, 'PAY-1004', 'Student dropped the course'),

    (7, 5, 'APPLIED',
     '2026-09-21 15:00:00', NULL,
     0.00, NULL, 'Waiting for approval'),

    (8, 5, 'APPROVED',
     '2026-09-19 13:00:00', '2026-09-20 09:00:00',
     8000.00, 'PAY-1005', 'Approved'),

    (9, 6, 'ACTIVE',
     '2026-09-15 10:00:00', '2026-09-16 10:00:00',
     7000.00, 'PAY-1006', 'Active enrollment'),

    (10, 7, 'COMPLETED',
     '2026-05-10 10:00:00', '2026-05-11 10:00:00',
     28000.00, 'PAY-1007', 'Completed successfully'),

    (11, 8, 'DROPPED',
     '2026-05-15 09:00:00', '2026-05-16 10:00:00',
     30000.00, 'PAY-1008', 'Student dropped'),

    (1, 9, 'ACTIVE',
     '2026-09-20 09:00:00', '2026-09-21 09:00:00',
     7500.00, 'PAY-1009', 'Active'),

    (2, 10, 'APPROVED',
     '2026-09-20 12:00:00', '2026-09-21 10:00:00',
     45000.00, 'PAY-1010', 'Approved'),

    (3, 11, 'APPLIED',
     '2026-09-22 11:00:00', NULL,
     0.00, NULL, 'Application pending'),

    (4, 12, 'ACTIVE',
     '2026-09-20 16:00:00', '2026-09-21 11:00:00',
     28000.00, 'PAY-1011', 'Active'),

    (5, 10, 'COMPLETED',
     '2026-04-10 10:00:00', '2026-04-11 09:00:00',
     45000.00, 'PAY-1012', 'Completed'),

    (6, 11, 'REJECTED',
     '2026-09-18 14:00:00', '2026-09-19 12:00:00',
     0.00, NULL, 'Application rejected'),

    (7, 12, 'DROPPED',
     '2026-09-10 10:00:00', '2026-09-11 10:00:00',
     28000.00, 'PAY-1013', 'Dropped by student');

-- =========================================================
-- EXAMS
-- =========================================================

INSERT INTO exams
(course_id, title, duration_minutes, total_marks, passing_marks, is_active)
VALUES
    (1, 'Core Java Assessment', 30, 10, 5, TRUE),
    (1, 'Spring Boot Assessment', 30, 10, 5, TRUE),
    (2, 'MySQL Database Assessment', 25, 10, 5, TRUE),
    (3, 'Web Development Assessment', 25, 10, 5, TRUE),
    (4, 'Aptitude Assessment', 30, 10, 6, FALSE);



-- =========================================================
-- QUESTIONS
-- 10 questions per exam
-- =========================================================

INSERT INTO questions
(exam_id, question_text, marks)
VALUES

-- EXAM 1 - CORE JAVA
(1, 'Which keyword is used to inherit a class in Java?', 1),
(1, 'Which method is the entry point of a Java application?', 1),
(1, 'Which collection does not allow duplicate elements?', 1),
(1, 'Which keyword prevents a class from being inherited?', 1),
(1, 'Which concept allows the same method name with different parameters?', 1),
(1, 'Which exception occurs when dividing an integer by zero?', 1),
(1, 'Which interface is commonly used for sorting objects?', 1),
(1, 'Which keyword is used to create an object?', 1),
(1, 'Which feature allows one class to implement multiple interfaces?', 1),
(1, 'Which package contains the String class?', 1),

-- EXAM 2 - SPRING BOOT
(2, 'Which annotation marks a Spring Boot application?', 1),
(2, 'Which annotation is used to create a REST controller?', 1),
(2, 'Which annotation maps HTTP GET requests?', 1),
(2, 'Which annotation is used for dependency injection?', 1),
(2, 'Which layer normally contains business logic?', 1),
(2, 'Which Spring module is commonly used for database persistence?', 1),
(2, 'Which annotation marks a JPA entity?', 1),
(2, 'Which HTTP status represents successful resource creation?', 1),
(2, 'Which mechanism is commonly used for stateless API authentication?', 1),
(2, 'Which file commonly stores Spring Boot configuration?', 1),

-- EXAM 3 - MYSQL
(3, 'Which SQL command is used to retrieve data?', 1),
(3, 'Which clause filters rows?', 1),
(3, 'Which key uniquely identifies a row?', 1),
(3, 'Which JOIN returns matching rows from both tables?', 1),
(3, 'Which clause groups rows for aggregate functions?', 1),
(3, 'Which function counts rows?', 1),
(3, 'Which index structure is commonly used for range queries in MySQL?', 1),
(3, 'Which command changes existing records?', 1),
(3, 'Which constraint prevents duplicate values?', 1),
(3, 'Which clause sorts query results?', 1),

-- EXAM 4 - WEB DEVELOPMENT
(4, 'Which HTML tag creates a hyperlink?', 1),
(4, 'Which CSS property changes text color?', 1),
(4, 'Which language is primarily used for browser-side logic?', 1),
(4, 'Which JavaScript keyword declares a block-scoped variable?', 1),
(4, 'Which API is commonly used to make HTTP requests in modern JavaScript?', 1),
(4, 'Which HTML element is used for an unordered list?', 1),
(4, 'Which CSS layout system uses rows and columns?', 1),
(4, 'Which event occurs when a user clicks an element?', 1),
(4, 'Which HTTP method is commonly used to retrieve data?', 1),
(4, 'Which HTML attribute provides alternative text for an image?', 1),

-- EXAM 5 - APTITUDE
(5, 'What is 20 percent of 200?', 1),
(5, 'What is the next number in 2, 4, 6, 8?', 1),
(5, 'If a product costs 100 and profit is 20 percent, what is selling price?', 1),
(5, 'What is the average of 10, 20 and 30?', 1),
(5, 'Which number is divisible by 3?', 1),
(5, 'If a train travels 60 km in 1 hour, what is its speed?', 1),
(5, 'What is the ratio of 2 to 4 in simplified form?', 1),
(5, 'What is 15 plus 25?', 1),
(5, 'If today is Monday, what day comes after 3 days?', 1),
(5, 'What is the square of 5?', 1);



-- =========================================================
-- OPTIONS
-- Exactly 4 options per question
-- Exactly 1 correct option per question
-- =========================================================

INSERT INTO options
(question_id, option_text, is_correct)
VALUES

-- Q1
(1, 'extends', TRUE),
(1, 'implements', FALSE),
(1, 'inherits', FALSE),
(1, 'super', FALSE),

-- Q2
(2, 'start()', FALSE),
(2, 'main()', TRUE),
(2, 'run()', FALSE),
(2, 'execute()', FALSE),

-- Q3
(3, 'List', FALSE),
(3, 'Set', TRUE),
(3, 'Queue', FALSE),
(3, 'ArrayList', FALSE),

-- Q4
(4, 'static', FALSE),
(4, 'final', TRUE),
(4, 'private', FALSE),
(4, 'const', FALSE),

-- Q5
(5, 'Inheritance', FALSE),
(5, 'Overloading', TRUE),
(5, 'Encapsulation', FALSE),
(5, 'Abstraction', FALSE),

-- Q6
(6, 'NullPointerException', FALSE),
(6, 'ArithmeticException', TRUE),
(6, 'IOException', FALSE),
(6, 'ClassNotFoundException', FALSE),

-- Q7
(7, 'Comparable', TRUE),
(7, 'Runnable', FALSE),
(7, 'Serializable', FALSE),
(7, 'Cloneable', FALSE),

-- Q8
(8, 'new', TRUE),
(8, 'create', FALSE),
(8, 'object', FALSE),
(8, 'instance', FALSE),

-- Q9
(9, 'Multiple inheritance through classes', FALSE),
(9, 'Multiple interfaces', TRUE),
(9, 'Multiple constructors only', FALSE),
(9, 'Multiple packages', FALSE),

-- Q10
(10, 'java.util', FALSE),
(10, 'java.lang', TRUE),
(10, 'java.io', FALSE),
(10, 'java.sql', FALSE),

-- Q11
(11, '@SpringBootApplication', TRUE),
(11, '@SpringApplication', FALSE),
(11, '@BootApplication', FALSE),
(11, '@Application', FALSE),

-- Q12
(12, '@RestController', TRUE),
(12, '@ControllerOnly', FALSE),
(12, '@RestAPI', FALSE),
(12, '@WebController', FALSE),

-- Q13
(13, '@GetMapping', TRUE),
(13, '@FetchMapping', FALSE),
(13, '@ReadMapping', FALSE),
(13, '@RequestGet', FALSE),

-- Q14
(14, '@Autowired', TRUE),
(14, '@InjectBean', FALSE),
(14, '@Dependency', FALSE),
(14, '@Wire', FALSE),

-- Q15
(15, 'Controller', FALSE),
(15, 'Service', TRUE),
(15, 'Entity', FALSE),
(15, 'Repository', FALSE),

-- Q16
(16, 'Spring Data JPA', TRUE),
(16, 'Spring MVC', FALSE),
(16, 'Spring Batch', FALSE),
(16, 'Spring Cloud', FALSE),

-- Q17
(17, '@Entity', TRUE),
(17, '@TableEntity', FALSE),
(17, '@JpaEntity', FALSE),
(17, '@DatabaseEntity', FALSE),

-- Q18
(18, '200 OK', FALSE),
(18, '201 Created', TRUE),
(18, '204 No Content', FALSE),
(18, '400 Bad Request', FALSE),

-- Q19
(19, 'JWT', TRUE),
(19, 'HTML', FALSE),
(19, 'CSS', FALSE),
(19, 'SQL', FALSE),

-- Q20
(20, 'application.properties', TRUE),
(20, 'index.html', FALSE),
(20, 'pom.lock', FALSE),
(20, 'config.xml', FALSE),

-- Q21
(21, 'SELECT', TRUE),
(21, 'GETROW', FALSE),
(21, 'FETCH', FALSE),
(21, 'READ', FALSE),

-- Q22
(22, 'WHERE', TRUE),
(22, 'FILTER', FALSE),
(22, 'HAVING', FALSE),
(22, 'LIMIT', FALSE),

-- Q23
(23, 'Primary Key', TRUE),
(23, 'Foreign Key', FALSE),
(23, 'Index', FALSE),
(23, 'View', FALSE),

-- Q24
(24, 'INNER JOIN', TRUE),
(24, 'OUTER JOIN', FALSE),
(24, 'CROSS JOIN', FALSE),
(24, 'SELF JOIN', FALSE),

-- Q25
(25, 'GROUP BY', TRUE),
(25, 'ORDER BY', FALSE),
(25, 'SORT BY', FALSE),
(25, 'COLLECT BY', FALSE),

-- Q26
(26, 'COUNT()', TRUE),
(26, 'TOTAL()', FALSE),
(26, 'ROWS()', FALSE),
(26, 'NUMBER()', FALSE),

-- Q27
(27, 'B-Tree', TRUE),
(27, 'Stack', FALSE),
(27, 'Queue', FALSE),
(27, 'Heap', FALSE),

-- Q28
(28, 'UPDATE', TRUE),
(28, 'CHANGE', FALSE),
(28, 'MODIFYROW', FALSE),
(28, 'ALTERROW', FALSE),

-- Q29
(29, 'UNIQUE', TRUE),
(29, 'DISTINCT', FALSE),
(29, 'ONLY', FALSE),
(29, 'SINGLE', FALSE),

-- Q30
(30, 'ORDER BY', TRUE),
(30, 'SORT', FALSE),
(30, 'GROUP SORT', FALSE),
(30, 'ARRANGE', FALSE),

-- Q31
(31, '<a>', TRUE),
(31, '<link>', FALSE),
(31, '<href>', FALSE),
(31, '<url>', FALSE),

-- Q32
(32, 'color', TRUE),
(32, 'font-color', FALSE),
(32, 'text-color', FALSE),
(32, 'foreground', FALSE),

-- Q33
(33, 'JavaScript', TRUE),
(33, 'SQL', FALSE),
(33, 'CSS', FALSE),
(33, 'XML', FALSE),

-- Q34
(34, 'let', TRUE),
(34, 'varonly', FALSE),
(34, 'define', FALSE),
(34, 'constant', FALSE),

-- Q35
(35, 'Fetch API', TRUE),
(35, 'SQL API', FALSE),
(35, 'HTML API', FALSE),
(35, 'Style API', FALSE),

-- Q36
(36, '<ul>', TRUE),
(36, '<ol>', FALSE),
(36, '<li>', FALSE),
(36, '<list>', FALSE),

-- Q37
(37, 'CSS Grid', TRUE),
(37, 'CSS Text', FALSE),
(37, 'CSS Font', FALSE),
(37, 'CSS Color', FALSE),

-- Q38
(38, 'click', TRUE),
(38, 'press', FALSE),
(38, 'touchonly', FALSE),
(38, 'select', FALSE),

-- Q39
(39, 'GET', TRUE),
(39, 'FETCH', FALSE),
(39, 'READ', FALSE),
(39, 'RETRIEVE', FALSE),

-- Q40
(40, 'alt', TRUE),
(40, 'src', FALSE),
(40, 'titleonly', FALSE),
(40, 'image-text', FALSE),

-- Q41
(41, '40', TRUE),
(41, '20', FALSE),
(41, '60', FALSE),
(41, '80', FALSE),

-- Q42
(42, '10', FALSE),
(42, '12', TRUE),
(42, '14', FALSE),
(42, '16', FALSE),

-- Q43
(43, '110', FALSE),
(43, '120', TRUE),
(43, '125', FALSE),
(43, '130', FALSE),

-- Q44
(44, '15', FALSE),
(44, '20', TRUE),
(44, '25', FALSE),
(44, '30', FALSE),

-- Q45
(45, '7', FALSE),
(45, '8', FALSE),
(45, '9', TRUE),
(45, '10', FALSE),

-- Q46
(46, '30 km/h', FALSE),
(46, '60 km/h', TRUE),
(46, '90 km/h', FALSE),
(46, '120 km/h', FALSE),

-- Q47
(47, '1:2', TRUE),
(47, '2:1', FALSE),
(47, '1:4', FALSE),
(47, '4:1', FALSE),

-- Q48
(48, '30', FALSE),
(48, '35', FALSE),
(48, '40', TRUE),
(48, '45', FALSE),

-- Q49
(49, 'Tuesday', FALSE),
(49, 'Wednesday', FALSE),
(49, 'Thursday', TRUE),
(49, 'Friday', FALSE),

-- Q50
(50, '10', FALSE),
(50, '20', FALSE),
(50, '25', TRUE),
(50, '30', FALSE);



-- =========================================================
-- ATTEMPTS
-- 9 rows
-- =========================================================

INSERT INTO attempts
(student_id, exam_id, started_at, submitted_at, score, status)
VALUES
    (2, 1, '2026-09-15 10:00:00', '2026-09-15 10:25:00', 7, 'SUBMITTED'),
    (2, 1, '2026-09-16 10:00:00', '2026-09-16 10:20:00', 3, 'SUBMITTED'),
    (2, 2, '2026-09-17 11:00:00', '2026-09-17 11:25:00', 8, 'SUBMITTED'),
    (2, 2, '2026-09-18 11:00:00', '2026-09-18 11:20:00', 4, 'SUBMITTED'),
    (4, 3, '2026-09-15 14:00:00', '2026-09-15 14:20:00', 7, 'SUBMITTED'),
    (4, 3, '2026-09-16 14:00:00', '2026-09-16 14:20:00', 3, 'SUBMITTED'),
    (6, 4, '2026-09-17 15:00:00', '2026-09-17 15:20:00', 6, 'SUBMITTED'),
    (6, 4, '2026-09-18 15:00:00', '2026-09-18 15:20:00', 4, 'SUBMITTED'),
    (2, 1, '2026-09-26 10:00:00', NULL, NULL, 'IN_PROGRESS');

-- =========================================================
-- ANSWERS
-- 80 rows
-- selected_option_id = NULL represents unanswered question
-- =========================================================

INSERT INTO answers
(attempt_id, question_id, selected_option_id)
VALUES

-- Attempt 1 : 7 correct / 2 wrong
(1, 1, 1),
(1, 2, 6),
(1, 3, 10),
(1, 4, 14),
(1, 5, 18),
(1, 6, 22),
(1, 7, 25),
(1, 8, 30),
(1, 9, 34),

-- Attempt 2 : 3 correct / 6 wrong
(2, 1, 1),
(2, 2, 6),
(2, 3, 10),
(2, 4, 13),
(2, 5, 17),
(2, 6, 21),
(2, 7, 26),
(2, 8, 29),
(2, 9, 33),

-- Attempt 3 : 8 correct / 1 wrong
(3, 11, 41),
(3, 12, 45),
(3, 13, 49),
(3, 14, 53),
(3, 15, 57),
(3, 16, 61),
(3, 17, 65),
(3, 18, 70),
(3, 19, 73),

-- Attempt 4 : 4 correct / 5 wrong
(4, 11, 41),
(4, 12, 45),
(4, 13, 49),
(4, 14, 53),
(4, 15, 58),
(4, 16, 62),
(4, 17, 66),
(4, 18, 69),
(4, 19, 74),

-- Attempt 5 : 7 correct / 2 wrong
(5, 21, 81),
(5, 22, 85),
(5, 23, 89),
(5, 24, 93),
(5, 25, 97),
(5, 26, 101),
(5, 27, 105),
(5, 28, 110),
(5, 29, 113),

-- Attempt 6 : 3 correct / 6 wrong
(6, 21, 81),
(6, 22, 85),
(6, 23, 89),
(6, 24, 94),
(6, 25, 98),
(6, 26, 102),
(6, 27, 106),
(6, 28, 109),
(6, 29, 114),

-- Attempt 7 : 6 correct / 3 wrong
(7, 31, 121),
(7, 32, 125),
(7, 33, 129),
(7, 34, 133),
(7, 35, 137),
(7, 36, 141),
(7, 37, 146),
(7, 38, 149),
(7, 39, 153),

-- Attempt 8 : 4 correct / 5 wrong
(8, 31, 121),
(8, 32, 125),
(8, 33, 129),
(8, 34, 133),
(8, 35, 138),
(8, 36, 142),
(8, 37, 145),
(8, 38, 150),
(8, 39, 154),

-- Attempt 9 : IN_PROGRESS
-- Question 8 deliberately unanswered
(9, 1, 1),
(9, 2, 6),
(9, 3, 10),
(9, 4, 14),
(9, 5, 18),
(9, 6, 21),
(9, 7, 26),
(9, 8, NULL);



-- =========================================================
-- COMPANIES
-- =========================================================

INSERT INTO companies
(name, industry, website, logo_url)
VALUES

    ('Tata Consultancy Services',
     'Information Technology',
     'https://www.tcs.com',
     'https://example.com/logos/tcs.png'),

    ('Infosys',
     'Information Technology',
     'https://www.infosys.com',
     'https://example.com/logos/infosys.png'),

    ('Wipro',
     'Information Technology',
     'https://www.wipro.com',
     'https://example.com/logos/wipro.png'),

    ('Persistent Systems',
     'Information Technology',
     'https://www.persistent.com',
     'https://example.com/logos/persistent.png'),

    ('Tech Mahindra',
     'Information Technology',
     'https://www.techmahindra.com',
     'https://example.com/logos/techmahindra.png'),

    ('Cognizant',
     'Information Technology',
     'https://www.cognizant.com',
     'https://example.com/logos/cognizant.png');



-- =========================================================
-- PLACEMENTS
-- 9 placements
-- Student 2 is deliberately placed twice
-- Salary range: 3.50 - 12.00 LPA
-- =========================================================

INSERT INTO placements
(student_id, company_id, course_id, job_title, package_lpa, placed_on)
VALUES

    (2, 1, 1,
     'Software Engineer',
     12.00,
     '2026-09-05'),

    (2, 2, 1,
     'Associate Software Engineer',
     8.00,
     '2026-09-12'),

    (4, 3, 2,
     'Junior Software Developer',
     7.00,
     '2026-08-20'),

    (5, 4, 1,
     'Backend Developer',
     6.50,
     '2026-08-25'),

    (6, 5, 3,
     'Software Trainee',
     5.50,
     '2026-08-28'),

    (7, 6, 4,
     'Associate Analyst',
     4.50,
     '2026-09-01'),

    (8, 1, 5,
     'Business Analyst',
     3.50,
     '2026-09-03'),

    (9, 2, 6,
     'Java Developer',
     4.90,
     '2026-09-06'),

    (10, 3, 6,
     'Software Engineer',
     3.90,
     '2026-09-10');


-- =========================================================
-- FEEDBACK
-- 14 records
-- 10 approved
-- 4 unapproved
-- =========================================================

INSERT INTO feedback
(student_id, course_id, rating, comments, is_approved, created_at)
VALUES

    (1, 1, 5,
     'Excellent Java course with practical examples.',
     TRUE,
     '2026-08-01 10:00:00'),

    (2, 1, 5,
     'Spring Boot projects were very useful.',
     TRUE,
     '2026-08-03 11:00:00'),

    (3, 2, 4,
     'Good introduction to database concepts.',
     TRUE,
     '2026-08-05 12:00:00'),

    (4, 2, 5,
     'The SQL practice sessions were excellent.',
     TRUE,
     '2026-08-07 09:30:00'),

    (5, 3, 4,
     'Good frontend fundamentals.',
     TRUE,
     '2026-08-09 14:00:00'),

    (6, 4, 5,
     'Aptitude practice helped with placement preparation.',
     TRUE,
     '2026-08-10 15:00:00'),

    (7, 5, 4,
     'Communication sessions were interactive.',
     TRUE,
     '2026-08-12 16:00:00'),

    (8, 6, 5,
     'Spring Boot concepts were explained clearly.',
     TRUE,
     '2026-08-15 10:00:00'),

    (9, 7, 3,
     'The Python course needs more practical exercises.',
     TRUE,
     '2026-08-18 13:00:00'),

    (10, 8, 4,
     'Useful verbal ability sessions.',
     TRUE,
     '2026-08-20 11:30:00'),

-- 4 unapproved records
    (11, 1, 5,
     'Very good course and trainers.',
     FALSE,
     '2026-09-01 10:00:00'),

    (1, 2, 4,
     'Good database practice material.',
     FALSE,
     '2026-09-02 11:00:00'),

    (2, 3, 5,
     'The web development sessions were helpful.',
     FALSE,
     '2026-09-03 12:00:00'),

    (4, 6, 4,
     'Good backend development content.',
     FALSE,
     '2026-09-04 13:00:00');