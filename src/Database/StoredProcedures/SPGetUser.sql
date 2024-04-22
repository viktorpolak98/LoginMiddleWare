USE [UserDb]
GO

/****** Object:  StoredProcedure [dbo].[GetUser]    Script Date: 2024-04-22 22:16:09 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[GetUser] @Name nvarchar(255)
As
Select Name from users 
WHERE Name = @Name
GO


