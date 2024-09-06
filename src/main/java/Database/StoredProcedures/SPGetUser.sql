USE [UserDb]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE procedure [dbo].[GetUser] @Username nvarchar(255)
As
Select Username from users 
WHERE Username = @Username
GO


