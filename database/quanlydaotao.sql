USE [QuanLyDaoTao]
GO

IF OBJECT_ID(N'[dbo].[academic_years]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[academic_years](
        [id] UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        [code] NVARCHAR(100) NULL,
        [name] NVARCHAR(100) NULL,
        [year] NVARCHAR(20) NULL,
        [description] NVARCHAR(255) NULL,
        [start_date] DATE NULL,
        [end_date] DATE NULL,
        [created_at] DATETIME2 NULL,
        [updated_at] DATETIME2 NULL,
        [created_by] UNIQUEIDENTIFIER NULL,
        [updated_by] UNIQUEIDENTIFIER NULL,
        [deleted_at] DATETIME2 NULL,
        [deleted_by] UNIQUEIDENTIFIER NULL,
        [is_active] BIT NULL
    )
END
GO

IF OBJECT_ID(N'[dbo].[school_years]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[school_years](
        [id] UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        [code] NVARCHAR(100) NULL,
        [name] NVARCHAR(255) NULL,
        [description] NVARCHAR(255) NULL,
        [note] NVARCHAR(255) NULL,
        [start_date] DATE NULL,
        [end_date] DATE NULL,
        [created_at] DATETIME2 NULL,
        [updated_at] DATETIME2 NULL,
        [created_by] UNIQUEIDENTIFIER NULL,
        [updated_by] UNIQUEIDENTIFIER NULL,
        [deleted_at] DATETIME2 NULL,
        [deleted_by] UNIQUEIDENTIFIER NULL,
        [is_active] BIT NULL
    )
END
GO

IF OBJECT_ID(N'[dbo].[semesters]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[semesters](
        [id] UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        [code] NVARCHAR(100) NULL,
        [name] NVARCHAR(255) NULL,
        [school_year_id] UNIQUEIDENTIFIER NULL,
        [school_year_name] NVARCHAR(20) NULL,
        [start_date] DATE NULL,
        [end_date] DATE NULL,
        [created_at] DATETIME2 NULL,
        [updated_at] DATETIME2 NULL,
        [created_by] UNIQUEIDENTIFIER NULL,
        [updated_by] UNIQUEIDENTIFIER NULL,
        [deleted_at] DATETIME2 NULL,
        [deleted_by] UNIQUEIDENTIFIER NULL,
        [is_active] BIT NULL,
        CONSTRAINT [FK_semesters_school_years] FOREIGN KEY ([school_year_id]) REFERENCES [dbo].[school_years]([id])
    )
END
GO

IF OBJECT_ID(N'[dbo].[course_sections]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[course_sections](
        [id] UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        [code] NVARCHAR(100) NULL,
        [name] NVARCHAR(255) NULL,
        [course_id] UNIQUEIDENTIFIER NULL,
        [semester_id] UNIQUEIDENTIFIER NULL,
        [room_id] UNIQUEIDENTIFIER NULL,
        [building_id] UNIQUEIDENTIFIER NULL,
        [max_students] INT NULL,
        [min_students] INT NULL,
        [class_type] NVARCHAR(255) NULL,
        [status] NVARCHAR(50) NULL,
        [registration_start] DATETIME2 NULL,
        [registration_end] DATETIME2 NULL,
        [note] NVARCHAR(255) NULL,
        [created_at] DATETIME2 NULL,
        [updated_at] DATETIME2 NULL,
        [created_by] UNIQUEIDENTIFIER NULL,
        [updated_by] UNIQUEIDENTIFIER NULL,
        [deleted_at] DATETIME2 NULL,
        [deleted_by] UNIQUEIDENTIFIER NULL,
        [is_active] BIT NULL,
        CONSTRAINT [FK_course_sections_semesters] FOREIGN KEY ([semester_id]) REFERENCES [dbo].[semesters]([id])
    )
END
GO

IF OBJECT_ID(N'[dbo].[student_course_sections]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[student_course_sections](
        [id] UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        [student_id] UNIQUEIDENTIFIER NULL,
        [course_section_id] UNIQUEIDENTIFIER NULL,
        [registration_date] DATETIME2 NULL,
        [registration_type] NVARCHAR(100) NULL,
        [status] NVARCHAR(100) NULL,
        [attempt_number] NVARCHAR(100) NULL,
        [note] NVARCHAR(255) NULL,
        [created_at] DATETIME2 NULL,
        [updated_at] DATETIME2 NULL,
        [created_by] UNIQUEIDENTIFIER NULL,
        [updated_by] UNIQUEIDENTIFIER NULL,
        [deleted_at] DATETIME2 NULL,
        [deleted_by] UNIQUEIDENTIFIER NULL,
        [is_active] BIT NULL,
        CONSTRAINT [FK_student_course_sections_course_sections] FOREIGN KEY ([course_section_id]) REFERENCES [dbo].[course_sections]([id])
    )
END
GO

IF OBJECT_ID(N'[dbo].[lecturer_course_classes]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[lecturer_course_classes](
        [id] UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        [employee_id] UNIQUEIDENTIFIER NULL,
        [course_class_id] UNIQUEIDENTIFIER NULL,
        [role] NVARCHAR(50) NULL,
        [created_at] DATETIME2 NULL,
        [updated_at] DATETIME2 NULL,
        [created_by] UNIQUEIDENTIFIER NULL,
        [updated_by] UNIQUEIDENTIFIER NULL,
        [deleted_at] DATETIME2 NULL,
        [deleted_by] UNIQUEIDENTIFIER NULL,
        [is_active] BIT NULL
    )
END
GO

IF OBJECT_ID(N'[dbo].[students]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[students](
        [id] UNIQUEIDENTIFIER NOT NULL CONSTRAINT [PK_students] PRIMARY KEY,
        [user_id] UNIQUEIDENTIFIER NULL,
        [code] NVARCHAR(255) NULL,
        [full_name] NVARCHAR(255) NULL,
        [email] NVARCHAR(255) NULL,
        [phone] NVARCHAR(20) NULL,
        [date_of_birth] DATE NULL,
        [gender] NVARCHAR(255) NULL,
        [personal_identification_number] NVARCHAR(255) NULL,
        [date_of_issue] DATE NULL,
        [card_place] NVARCHAR(255) NULL,
        [address] NVARCHAR(255) NULL,
        [current_address] NVARCHAR(255) NULL,
        [academic_year_year] DATETIME2 NULL,
        [department_id] UNIQUEIDENTIFIER NULL,
        [major_id] UNIQUEIDENTIFIER NULL,
        [training_program_id] UNIQUEIDENTIFIER NULL,
        [status_id] UNIQUEIDENTIFIER NULL,
        [student_classess_id] UNIQUEIDENTIFIER NULL,
        [admission_year] DATETIME2 NULL,
        [created_at] DATETIME2 NULL,
        [updated_at] DATETIME2 NULL,
        [created_by] UNIQUEIDENTIFIER NULL,
        [updated_by] UNIQUEIDENTIFIER NULL,
        [deleted_at] DATETIME2 NULL,
        [deleted_by] UNIQUEIDENTIFIER NULL,
        [is_active] BIT NULL
    )
END
GO

IF COL_LENGTH(N'dbo.students', N'code') IS NULL
   AND COL_LENGTH(N'dbo.students', N'student_code') IS NOT NULL
BEGIN
    EXEC sys.sp_rename N'dbo.students.student_code', N'code', N'COLUMN'
END
GO

IF COL_LENGTH(N'dbo.students', N'code') IS NULL
    ALTER TABLE [dbo].[students] ADD [code] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'user_id') IS NULL
    ALTER TABLE [dbo].[students] ADD [user_id] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'full_name') IS NULL
    ALTER TABLE [dbo].[students] ADD [full_name] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'email') IS NULL
    ALTER TABLE [dbo].[students] ADD [email] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'phone') IS NULL
    ALTER TABLE [dbo].[students] ADD [phone] NVARCHAR(20) NULL
IF COL_LENGTH(N'dbo.students', N'date_of_birth') IS NULL
    ALTER TABLE [dbo].[students] ADD [date_of_birth] DATE NULL
IF COL_LENGTH(N'dbo.students', N'gender') IS NULL
    ALTER TABLE [dbo].[students] ADD [gender] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'personal_identification_number') IS NULL
    ALTER TABLE [dbo].[students] ADD [personal_identification_number] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'date_of_issue') IS NULL
    ALTER TABLE [dbo].[students] ADD [date_of_issue] DATE NULL
IF COL_LENGTH(N'dbo.students', N'card_place') IS NULL
    ALTER TABLE [dbo].[students] ADD [card_place] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'address') IS NULL
    ALTER TABLE [dbo].[students] ADD [address] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'current_address') IS NULL
    ALTER TABLE [dbo].[students] ADD [current_address] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'academic_year_year') IS NULL
    ALTER TABLE [dbo].[students] ADD [academic_year_year] DATETIME2 NULL
IF COL_LENGTH(N'dbo.students', N'department_id') IS NULL
    ALTER TABLE [dbo].[students] ADD [department_id] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'major_id') IS NULL
    ALTER TABLE [dbo].[students] ADD [major_id] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'training_program_id') IS NULL
    ALTER TABLE [dbo].[students] ADD [training_program_id] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'status_id') IS NULL
    ALTER TABLE [dbo].[students] ADD [status_id] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'student_classess_id') IS NULL
    ALTER TABLE [dbo].[students] ADD [student_classess_id] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'admission_year') IS NULL
    ALTER TABLE [dbo].[students] ADD [admission_year] DATETIME2 NULL
IF COL_LENGTH(N'dbo.students', N'created_at') IS NULL
    ALTER TABLE [dbo].[students] ADD [created_at] DATETIME2 NULL
IF COL_LENGTH(N'dbo.students', N'updated_at') IS NULL
    ALTER TABLE [dbo].[students] ADD [updated_at] DATETIME2 NULL
IF COL_LENGTH(N'dbo.students', N'created_by') IS NULL
    ALTER TABLE [dbo].[students] ADD [created_by] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'updated_by') IS NULL
    ALTER TABLE [dbo].[students] ADD [updated_by] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'deleted_at') IS NULL
    ALTER TABLE [dbo].[students] ADD [deleted_at] DATETIME2 NULL
IF COL_LENGTH(N'dbo.students', N'deleted_by') IS NULL
    ALTER TABLE [dbo].[students] ADD [deleted_by] UNIQUEIDENTIFIER NULL
IF COL_LENGTH(N'dbo.students', N'is_active') IS NULL
    ALTER TABLE [dbo].[students] ADD [is_active] BIT NULL
GO

IF EXISTS (SELECT 1 FROM sys.key_constraints
           WHERE [name] = N'UQ_students_student_code'
             AND [parent_object_id] = OBJECT_ID(N'[dbo].[students]'))
    ALTER TABLE [dbo].[students] DROP CONSTRAINT [UQ_students_student_code]
IF EXISTS (SELECT 1 FROM sys.indexes
           WHERE [name] = N'UQ_students_code'
             AND [object_id] = OBJECT_ID(N'[dbo].[students]'))
    DROP INDEX [UQ_students_code] ON [dbo].[students]
IF EXISTS (SELECT 1 FROM sys.columns
           WHERE [object_id] = OBJECT_ID(N'[dbo].[students]')
             AND [name] = N'code' AND [max_length] < 510)
    ALTER TABLE [dbo].[students] ALTER COLUMN [code] NVARCHAR(255) NULL
GO

IF EXISTS (SELECT 1 FROM sys.key_constraints
           WHERE [name] = N'UQ_students_email'
             AND [parent_object_id] = OBJECT_ID(N'[dbo].[students]'))
    ALTER TABLE [dbo].[students] DROP CONSTRAINT [UQ_students_email]
GO

IF COL_LENGTH(N'dbo.students', N'email') IS NOT NULL
   AND EXISTS (SELECT 1 FROM sys.columns
               WHERE [object_id] = OBJECT_ID(N'[dbo].[students]')
                 AND [name] = N'email' AND [is_nullable] = 0)
    ALTER TABLE [dbo].[students] ALTER COLUMN [email] NVARCHAR(255) NULL
IF COL_LENGTH(N'dbo.students', N'phone') IS NOT NULL
   AND EXISTS (SELECT 1 FROM sys.columns
               WHERE [object_id] = OBJECT_ID(N'[dbo].[students]')
                 AND [name] = N'phone' AND [is_nullable] = 0)
    ALTER TABLE [dbo].[students] ALTER COLUMN [phone] NVARCHAR(20) NULL
IF COL_LENGTH(N'dbo.students', N'class_name') IS NOT NULL
   AND EXISTS (SELECT 1 FROM sys.columns
               WHERE [object_id] = OBJECT_ID(N'[dbo].[students]')
                 AND [name] = N'class_name' AND [is_nullable] = 0)
    ALTER TABLE [dbo].[students] ALTER COLUMN [class_name] NVARCHAR(100) NULL
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes
               WHERE [name] = N'UQ_students_code'
                 AND [object_id] = OBJECT_ID(N'[dbo].[students]'))
    CREATE UNIQUE INDEX [UQ_students_code]
        ON [dbo].[students]([code]) WHERE [code] IS NOT NULL
IF NOT EXISTS (SELECT 1 FROM sys.indexes
               WHERE [name] = N'UQ_students_email'
                 AND [object_id] = OBJECT_ID(N'[dbo].[students]'))
    CREATE UNIQUE INDEX [UQ_students_email]
        ON [dbo].[students]([email]) WHERE [email] IS NOT NULL
GO

/****** Object:  Data [dbo].[students] ******/
IF NOT EXISTS (SELECT 1 FROM [dbo].[students])
BEGIN
INSERT [dbo].[students]
    ([id], [code], [full_name], [email], [phone])
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', N'SV001', N'Nguyễn Văn A',  N'a@gmail.com', '0901234567'),
    ('550e8400-e29b-41d4-a716-446655440002', N'SV002', N'Trần Thị B',    N'b@gmail.com', '0912345678'),
    ('550e8400-e29b-41d4-a716-446655440003', N'SV003', N'Lê Hoàng C',    N'c@gmail.com', '0923456789'),
    ('550e8400-e29b-41d4-a716-446655440004', N'SV004', N'Phạm Minh D',   N'd@gmail.com', '0933456789'),
    ('550e8400-e29b-41d4-a716-446655440005', N'SV005', N'Hoàng Thị E',   N'e@gmail.com', '0944567890'),
    ('550e8400-e29b-41d4-a716-446655440006', N'SV006', N'Vũ Đức F',      N'f@gmail.com', '0955678901'),
    ('550e8400-e29b-41d4-a716-446655440007', N'SV007', N'Đặng Nhật G',   N'g@gmail.com', '0966789012'),
    ('550e8400-e29b-41d4-a716-446655440008', N'SV008', N'Bùi Thảo H',    N'h@gmail.com', '0977890123'),
    ('550e8400-e29b-41d4-a716-446655440009', N'SV009', N'Ngô Quốc I',    N'i@gmail.com', '0988901234'),
    ('550e8400-e29b-41d4-a716-446655440010', N'SV010', N'Mai Lan K',     N'k@gmail.com', '0999012345'),
    ('550e8400-e29b-41d4-a716-446655440011', N'SV011', N'Nguyễn Thị L',  N'l@gmail.com', '0901122334'),
    ('550e8400-e29b-41d4-a716-446655440012', N'SV012', N'Trần Văn M',    N'm@gmail.com', '0912233445'),
    ('550e8400-e29b-41d4-a716-446655440013', N'SV013', N'Lê Minh N',     N'n@gmail.com', '0923344556'),
    ('550e8400-e29b-41d4-a716-446655440014', N'SV014', N'Phạm Thị O',    N'o@gmail.com', '0934455667'),
    ('550e8400-e29b-41d4-a716-446655440015', N'SV015', N'Hoàng Văn P',   N'p@gmail.com', '0945566778'),
    ('550e8400-e29b-41d4-a716-446655440016', N'SV016', N'Vũ Thị Q',      N'q@gmail.com', '0956677889'),
    ('550e8400-e29b-41d4-a716-446655440017', N'SV017', N'Đặng Văn R',    N'r@gmail.com', '0967788990'),
    ('550e8400-e29b-41d4-a716-446655440018', N'SV018', N'Bùi Minh S',    N's@gmail.com', '0978899001'),
    ('550e8400-e29b-41d4-a716-446655440019', N'SV019', N'Ngô Thị T',     N't@gmail.com', '0989900112'),
    ('550e8400-e29b-41d4-a716-446655440020', N'SV020', N'Mai Hoàng U',   N'u@gmail.com', '0990011223')
END
GO

IF OBJECT_ID(N'[dbo].[student_course_sections]', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_student_course_sections_students')
BEGIN
    ALTER TABLE [dbo].[student_course_sections]
    ADD CONSTRAINT [FK_student_course_sections_students]
        FOREIGN KEY ([student_id]) REFERENCES [dbo].[students]([id])
END
GO