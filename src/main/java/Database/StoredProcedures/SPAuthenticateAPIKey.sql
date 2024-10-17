USE [UserDb]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[AuthenticateAPIKey] @EmailAddress nvarchar(255), @APIKey nvarchar(195), @Is_valid BIT OUTPUT
As
	DECLARE @stored_hash NVARCHAR(195);
    DECLARE @entered_hash NVARCHAR(195);
	DECLARE @APIUser NVARCHAR(255);
	DECLARE @ValidTo date;

	SET @entered_hash = HASHBYTES('SHA2_512', @APIKey);

    SELECT @stored_hash = APIKey, @APIUser = EmailAddress, @ValidTo = ValidTo
    FROM [dbo].APIKeys
    WHERE APIKey = @entered_hash

    IF @entered_hash = @stored_hash AND @APIUser = @EmailAddress AND (@ValidTo >= GETDATE() OR @ValidTo IS NULL)
        SET @Is_valid = 1;
    ELSE
        SET @Is_valid = 0;

GO