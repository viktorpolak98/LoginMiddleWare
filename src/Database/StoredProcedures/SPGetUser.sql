USE [UserDb]
GO

/****** Object:  StoredProcedure [dbo].[GetUser]    Script Date: 2024-04-25 19:29:57 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE procedure [dbo].[GetUser] @Username nvarchar(255)
As
Select Username from users 
WHERE Username = @Username
GO


