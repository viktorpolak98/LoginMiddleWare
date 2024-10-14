USE [UserDb]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE procedure [dbo].[InvalidateKey] @EmailAddress nvarchar(255), @APIKey nvarchar(195)
As
    DECLARE @yesterday date = DATEADD(day, -1, CAST(GETDATE() AS date));
	DECLARE @hashedKey nvarchar(195) = HASHBYTES('SHA2_512', @APIKey);

    UPDATE APIKeys
    SET ValidTo = @yesterday
    WHERE APIKey = @hashedKey AND EmailAddress = @EmailAddress;

GO