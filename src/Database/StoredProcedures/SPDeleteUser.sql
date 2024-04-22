USE [UserDb]
GO

/****** Object:  StoredProcedure [dbo].[DeleteUser]    Script Date: 2024-04-22 22:16:04 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[DeleteUser] @Name Nvarchar(255)
As
Delete from users where Name = @Name
GO


