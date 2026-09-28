CREATE DATABASE IF NOT EXISTS techedge
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE techedge;







-- =========================================================
-- AUTH
-- =========================================================

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       phone VARCHAR(15),
                       role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE students (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT NOT NULL UNIQUE,
                          qualification VARCHAR(100),
                          graduation_year INT,
                          city VARCHAR(80),
                          resume_url VARCHAR(255),

                          CONSTRAINT fk_student_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id)
                                  ON DELETE CASCADE
);


-- =========================================================
-- CATALOGUE
-- =========================================================

CREATE TABLE courses (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         code VARCHAR(20) NOT NULL UNIQUE,
                         title VARCHAR(150) NOT NULL,
                         description TEXT,
                         category ENUM(
        'TECHNICAL',
        'APTITUDE',
        'COMMUNICATION'
    ) NOT NULL,
                         duration_weeks INT NOT NULL,
                         fee DECIMAL(10,2) NOT NULL,
                         level ENUM(
        'BEGINNER',
        'INTERMEDIATE',
        'ADVANCED'
    ) NOT NULL,
                         is_active BOOLEAN NOT NULL DEFAULT TRUE,

                         INDEX idx_course_category (category)
);


CREATE TABLE topics (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        course_id BIGINT NOT NULL,
                        title VARCHAR(150) NOT NULL,
                        sort_order INT NOT NULL,

                        CONSTRAINT fk_topic_course
                            FOREIGN KEY (course_id)
                                REFERENCES courses(id)
                                ON DELETE CASCADE
);


CREATE TABLE batches (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         course_id BIGINT NOT NULL,
                         batch_code VARCHAR(30) NOT NULL UNIQUE,
                         start_date DATE NOT NULL,
                         end_date DATE NOT NULL,
                         timing VARCHAR(50),
                         mode ENUM(
        'ONLINE',
        'OFFLINE',
        'HYBRID'
    ) NOT NULL,
                         trainer_name VARCHAR(100),
                         total_seats INT NOT NULL,
                         available_seats INT NOT NULL,
                         status ENUM(
        'UPCOMING',
        'ONGOING',
        'COMPLETED'
    ) NOT NULL DEFAULT 'UPCOMING',

                         CONSTRAINT fk_batch_course
                             FOREIGN KEY (course_id)
                                 REFERENCES courses(id),

                         CONSTRAINT chk_seats
                             CHECK (
                                 available_seats >= 0
                                     AND available_seats <= total_seats
                                 )
);


-- =========================================================
-- OFFERS
-- =========================================================

CREATE TABLE offers (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(150) NOT NULL,
                        description TEXT,
                        discount_percent DECIMAL(5,2) NOT NULL,
                        coupon_code VARCHAR(30) UNIQUE,
                        valid_from DATE NOT NULL,
                        valid_to DATE NOT NULL,
                        is_active BOOLEAN NOT NULL DEFAULT TRUE,

                        CONSTRAINT chk_discount
                            CHECK (
                                discount_percent > 0
                                    AND discount_percent <= 100
                                ),

                        CONSTRAINT chk_dates
                            CHECK (valid_to >= valid_from)
);


CREATE TABLE course_offers (
                               course_id BIGINT NOT NULL,
                               offer_id BIGINT NOT NULL,

                               PRIMARY KEY (course_id, offer_id),

                               CONSTRAINT fk_co_course
                                   FOREIGN KEY (course_id)
                                       REFERENCES courses(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_co_offer
                                   FOREIGN KEY (offer_id)
                                       REFERENCES offers(id)
                                       ON DELETE CASCADE
);


-- =========================================================
-- ENROLLMENT
-- =========================================================

CREATE TABLE enrollments (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             student_id BIGINT NOT NULL,
                             batch_id BIGINT NOT NULL,

                             status ENUM(
        'APPLIED',
        'APPROVED',
        'REJECTED',
        'ACTIVE',
        'COMPLETED',
        'DROPPED'
    ) NOT NULL DEFAULT 'APPLIED',

                             applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             decided_at TIMESTAMP NULL,
                             fee_paid DECIMAL(10,2) DEFAULT 0.00,
                             payment_ref VARCHAR(60),
                             remarks VARCHAR(255),

                             CONSTRAINT fk_enr_student
                                 FOREIGN KEY (student_id)
                                     REFERENCES students(id),

                             CONSTRAINT fk_enr_batch
                                 FOREIGN KEY (batch_id)
                                     REFERENCES batches(id),

                             CONSTRAINT uq_student_batch
                                 UNIQUE (student_id, batch_id)
);


-- =========================================================
-- EXAM
-- =========================================================

CREATE TABLE exams (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       course_id BIGINT NOT NULL,
                       title VARCHAR(150) NOT NULL,
                       duration_minutes INT NOT NULL,
                       total_marks INT NOT NULL,
                       passing_marks INT NOT NULL,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,

                       CONSTRAINT fk_exam_course
                           FOREIGN KEY (course_id)
                               REFERENCES courses(id)
);


CREATE TABLE questions (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           exam_id BIGINT NOT NULL,
                           question_text TEXT NOT NULL,
                           marks INT NOT NULL DEFAULT 1,

                           CONSTRAINT fk_q_exam
                               FOREIGN KEY (exam_id)
                                   REFERENCES exams(id)
                                   ON DELETE CASCADE
);


CREATE TABLE options (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         question_id BIGINT NOT NULL,
                         option_text VARCHAR(500) NOT NULL,
                         is_correct BOOLEAN NOT NULL DEFAULT FALSE,

                         CONSTRAINT fk_opt_question
                             FOREIGN KEY (question_id)
                                 REFERENCES questions(id)
                                 ON DELETE CASCADE
);


CREATE TABLE attempts (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          student_id BIGINT NOT NULL,
                          exam_id BIGINT NOT NULL,
                          started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          submitted_at TIMESTAMP NULL,
                          score INT,

                          status ENUM(
        'IN_PROGRESS',
        'SUBMITTED',
        'EXPIRED'
    ) NOT NULL DEFAULT 'IN_PROGRESS',

                          CONSTRAINT fk_att_student
                              FOREIGN KEY (student_id)
                                  REFERENCES students(id),

                          CONSTRAINT fk_att_exam
                              FOREIGN KEY (exam_id)
                                  REFERENCES exams(id),

                          INDEX idx_attempt_student (student_id, exam_id)
);


CREATE TABLE answers (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         attempt_id BIGINT NOT NULL,
                         question_id BIGINT NOT NULL,
                         selected_option_id BIGINT,

                         CONSTRAINT fk_ans_attempt
                             FOREIGN KEY (attempt_id)
                                 REFERENCES attempts(id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_ans_question
                             FOREIGN KEY (question_id)
                                 REFERENCES questions(id),

                         CONSTRAINT fk_ans_option
                             FOREIGN KEY (selected_option_id)
                                 REFERENCES options(id),

                         CONSTRAINT uq_attempt_question
                             UNIQUE (attempt_id, question_id)
);


-- =========================================================
-- PLACEMENT
-- =========================================================

CREATE TABLE companies (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(150) NOT NULL UNIQUE,
                           industry VARCHAR(100),
                           website VARCHAR(255),
                           logo_url VARCHAR(255)
);


CREATE TABLE placements (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            student_id BIGINT NOT NULL,
                            company_id BIGINT NOT NULL,
                            course_id BIGINT,
                            job_title VARCHAR(120) NOT NULL,
                            package_lpa DECIMAL(5,2),
                            placed_on DATE NOT NULL,

                            CONSTRAINT fk_pl_student
                                FOREIGN KEY (student_id)
                                    REFERENCES students(id),

                            CONSTRAINT fk_pl_company
                                FOREIGN KEY (company_id)
                                    REFERENCES companies(id),

                            CONSTRAINT fk_pl_course
                                FOREIGN KEY (course_id)
                                    REFERENCES courses(id)
);


-- =========================================================
-- FEEDBACK
-- =========================================================

CREATE TABLE feedback (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          student_id BIGINT NOT NULL,
                          course_id BIGINT NOT NULL,
                          rating INT NOT NULL,
                          comments TEXT,
                          is_approved BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_fb_student
                              FOREIGN KEY (student_id)
                                  REFERENCES students(id),

                          CONSTRAINT fk_fb_course
                              FOREIGN KEY (course_id)
                                  REFERENCES courses(id),

                          CONSTRAINT chk_rating
                              CHECK (rating BETWEEN 1 AND 5)
);