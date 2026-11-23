USE [quanlydaotao];

IF DB_NAME() <> N'quanlydaotao'
BEGIN
    THROW 50000, 'Run this migration against the quanlydaotao database.', 1;
END;

IF OBJECT_ID(N'[dbo].[students]', N'U') IS NULL
BEGIN
    THROW 50001, 'Required table dbo.students does not exist.', 1;
END;

IF OBJECT_ID(N'[dbo].[lecturer_course_classes]', N'U') IS NULL
BEGIN
    THROW 50002, 'Required table dbo.lecturer_course_classes does not exist.', 1;
END;

IF OBJECT_ID(N'[dbo].[student_classes]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[academic_years]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[training_programs]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[majors]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[departments]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[course_sections]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[courses]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[rooms]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[buildings]', N'U') IS NULL
   OR OBJECT_ID(N'[dbo].[student_course_sections]', N'U') IS NULL
BEGIN
    THROW 50012, 'A required academic catalog or registration table is missing.', 1;
END;

SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF COL_LENGTH(N'dbo.students', N'email') IS NULL
        ALTER TABLE [dbo].[students] ADD [email] NVARCHAR(255) NULL;

    IF COL_LENGTH(N'dbo.students', N'phone') IS NULL
        ALTER TABLE [dbo].[students] ADD [phone] NVARCHAR(20) NULL;

    IF COL_LENGTH(N'dbo.lecturer_course_classes', N'employee_id') IS NULL
        ALTER TABLE [dbo].[lecturer_course_classes] ADD [employee_id] UNIQUEIDENTIFIER NULL;

    ;WITH ranked_years AS (
        SELECT id, is_active, deleted_at, updated_at,
               ROW_NUMBER() OVER (
                   PARTITION BY id
                   ORDER BY CASE
                       WHEN deleted_at IS NULL AND (is_active = 1 OR is_active IS NULL) THEN 0
                       ELSE 1
                   END, created_at, updated_at
               ) AS row_number
        FROM dbo.academic_years
    )
    UPDATE ranked_years
    SET id = NEWID(),
        is_active = 0,
        deleted_at = COALESCE(deleted_at, SYSUTCDATETIME()),
        updated_at = SYSUTCDATETIME()
    WHERE row_number > 1;

    IF EXISTS (
        SELECT id
        FROM dbo.academic_years
        GROUP BY id
        HAVING id IS NULL OR COUNT_BIG(*) > 1
    )
    BEGIN
        THROW 50014, 'academic_years still contains null or duplicate IDs; no academic-year foreign key was added.', 1;
    END;

    IF NOT EXISTS (
        SELECT 1
        FROM sys.key_constraints
        WHERE parent_object_id = OBJECT_ID(N'dbo.academic_years')
          AND type = N'PK'
    )
        ALTER TABLE dbo.academic_years
            ADD CONSTRAINT PK_academic_years PRIMARY KEY (id);

    IF EXISTS (
        SELECT 1
        FROM dbo.student_classes AS c
        LEFT JOIN dbo.academic_years AS y ON y.id = c.academic_year_id
        WHERE c.academic_year_id IS NOT NULL AND y.id IS NULL
    )
    BEGIN
        THROW 50003, 'Orphan student_classes.academic_year_id values exist; no foreign keys were changed.', 1;
    END;

    IF EXISTS (
        SELECT 1
        FROM dbo.student_classes AS c
        LEFT JOIN dbo.training_programs AS p ON p.id = c.training_program_id
        WHERE c.training_program_id IS NOT NULL AND p.id IS NULL
    )
    BEGIN
        THROW 50004, 'Orphan student_classes.training_program_id values exist; no foreign keys were changed.', 1;
    END;

    IF EXISTS (
        SELECT 1
        FROM dbo.students AS s
        LEFT JOIN dbo.training_programs AS p ON p.id = s.training_program_id
        WHERE s.training_program_id IS NOT NULL AND p.id IS NULL
    )
    BEGIN
        THROW 50005, 'Orphan students.training_program_id values exist; no foreign keys were changed.', 1;
    END;

    IF EXISTS (
        SELECT 1
        FROM dbo.course_sections AS cs
        LEFT JOIN dbo.courses AS c ON c.id = cs.course_id
        WHERE cs.course_id IS NOT NULL AND c.id IS NULL
    )
    BEGIN
        THROW 50007, 'Orphan course_sections.course_id values exist; no foreign keys were changed.', 1;
    END;

    IF EXISTS (
        SELECT 1
        FROM dbo.course_sections AS cs
        LEFT JOIN dbo.rooms AS r ON r.id = cs.room_id
        WHERE cs.room_id IS NOT NULL AND r.id IS NULL
    )
    BEGIN
        THROW 50008, 'Orphan course_sections.room_id values exist; no foreign keys were changed.', 1;
    END;

    IF EXISTS (
        SELECT 1
        FROM dbo.course_sections AS cs
        LEFT JOIN dbo.buildings AS b ON b.id = cs.building_id
        WHERE cs.building_id IS NOT NULL AND b.id IS NULL
    )
    BEGIN
        THROW 50009, 'Orphan course_sections.building_id values exist; no foreign keys were changed.', 1;
    END;

    IF EXISTS (
        SELECT 1
        FROM dbo.student_course_sections AS r
        LEFT JOIN dbo.students AS s ON s.id = r.student_id
        WHERE r.student_id IS NOT NULL AND s.id IS NULL
    )
    BEGIN
        THROW 50010, 'Orphan student_course_sections.student_id values exist; no foreign keys were changed.', 1;
    END;

    IF EXISTS (
        SELECT 1
        FROM dbo.student_course_sections AS r
        LEFT JOIN dbo.course_sections AS cs ON cs.id = r.course_section_id
        WHERE r.course_section_id IS NOT NULL AND cs.id IS NULL
    )
    BEGIN
        THROW 50011, 'Orphan student_course_sections.course_section_id values exist; no foreign keys were changed.', 1;
    END;

    IF EXISTS (
        SELECT 1
        FROM dbo.lecturer_course_classes AS assignment
        LEFT JOIN dbo.course_sections AS section ON section.id = assignment.course_class_id
        WHERE assignment.course_class_id IS NOT NULL AND section.id IS NULL
    )
    BEGIN
        THROW 50013, 'Orphan lecturer_course_classes.course_class_id values exist; no foreign keys were changed.', 1;
    END;

    DECLARE @constraintName sysname;
    DECLARE @dropConstraintSql nvarchar(4000);
    WHILE EXISTS (
        SELECT 1
        FROM sys.foreign_keys AS fk
        JOIN sys.foreign_key_columns AS fkc ON fkc.constraint_object_id = fk.object_id
        WHERE fk.parent_object_id = OBJECT_ID(N'dbo.lecturer_course_classes')
          AND fk.referenced_object_id <> OBJECT_ID(N'dbo.course_sections')
          AND fkc.parent_column_id = COLUMNPROPERTY(
                OBJECT_ID(N'dbo.lecturer_course_classes'), N'course_class_id', 'ColumnId')
    )
    BEGIN
        SELECT TOP (1) @constraintName = fk.name
        FROM sys.foreign_keys AS fk
        JOIN sys.foreign_key_columns AS fkc ON fkc.constraint_object_id = fk.object_id
        WHERE fk.parent_object_id = OBJECT_ID(N'dbo.lecturer_course_classes')
          AND fk.referenced_object_id <> OBJECT_ID(N'dbo.course_sections')
          AND fkc.parent_column_id = COLUMNPROPERTY(
                OBJECT_ID(N'dbo.lecturer_course_classes'), N'course_class_id', 'ColumnId');

        SET @dropConstraintSql = N'ALTER TABLE dbo.lecturer_course_classes DROP CONSTRAINT '
            + QUOTENAME(@constraintName);
        EXEC sys.sp_executesql @dropConstraintSql;
    END;

    IF NOT EXISTS (
        SELECT 1
        FROM sys.foreign_keys AS fk
        JOIN sys.foreign_key_columns AS fkc ON fkc.constraint_object_id = fk.object_id
        WHERE fk.parent_object_id = OBJECT_ID(N'dbo.lecturer_course_classes')
          AND fk.referenced_object_id = OBJECT_ID(N'dbo.course_sections')
          AND fkc.parent_column_id = COLUMNPROPERTY(
                OBJECT_ID(N'dbo.lecturer_course_classes'), N'course_class_id', 'ColumnId')
    )
        ALTER TABLE dbo.lecturer_course_classes WITH CHECK
            ADD CONSTRAINT FK_lecturer_course_classes_course_section
            FOREIGN KEY (course_class_id) REFERENCES dbo.course_sections(id);

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_student_classes_academic_year')
        ALTER TABLE dbo.student_classes WITH CHECK
            ADD CONSTRAINT FK_student_classes_academic_year
            FOREIGN KEY (academic_year_id) REFERENCES dbo.academic_years(id);

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_student_classes_training_program')
        ALTER TABLE dbo.student_classes WITH CHECK
            ADD CONSTRAINT FK_student_classes_training_program
            FOREIGN KEY (training_program_id) REFERENCES dbo.training_programs(id);

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_students_training_program')
        ALTER TABLE dbo.students WITH CHECK
            ADD CONSTRAINT FK_students_training_program
            FOREIGN KEY (training_program_id) REFERENCES dbo.training_programs(id);

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_course_sections_course')
        ALTER TABLE dbo.course_sections WITH CHECK
            ADD CONSTRAINT FK_course_sections_course
            FOREIGN KEY (course_id) REFERENCES dbo.courses(id);

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_course_sections_room')
        ALTER TABLE dbo.course_sections WITH CHECK
            ADD CONSTRAINT FK_course_sections_room
            FOREIGN KEY (room_id) REFERENCES dbo.rooms(id);

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_course_sections_building')
        ALTER TABLE dbo.course_sections WITH CHECK
            ADD CONSTRAINT FK_course_sections_building
            FOREIGN KEY (building_id) REFERENCES dbo.buildings(id);

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_student_course_sections_student')
        ALTER TABLE dbo.student_course_sections WITH CHECK
            ADD CONSTRAINT FK_student_course_sections_student
            FOREIGN KEY (student_id) REFERENCES dbo.students(id);

    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_student_course_sections_course_section')
        ALTER TABLE dbo.student_course_sections WITH CHECK
            ADD CONSTRAINT FK_student_course_sections_course_section
            FOREIGN KEY (course_section_id) REFERENCES dbo.course_sections(id);

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;

SELECT
    OBJECT_NAME(c.object_id) AS table_name,
    c.name AS column_name,
    TYPE_NAME(c.user_type_id) AS data_type,
    c.is_nullable
FROM sys.columns AS c
WHERE c.object_id IN (
    OBJECT_ID(N'[dbo].[students]'),
    OBJECT_ID(N'[dbo].[lecturer_course_classes]')
)
AND c.name IN (N'email', N'phone', N'employee_id')
ORDER BY table_name, column_name;
