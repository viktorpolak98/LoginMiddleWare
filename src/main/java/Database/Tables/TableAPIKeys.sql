USE [UserDb]
GO

ALTER TABLE [dbo].[APIKeys] DROP CONSTRAINT [FK__APIKeys__Usernam__787EE5A0]
GO

/****** Object:  Table [dbo].[APIKeys]    Script Date: 2024-09-01 20:50:39 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[APIKeys]') AND type in (N'U'))
DROP TABLE [dbo].[APIKeys]
GO

/****** Object:  Table [dbo].[APIKeys]    Script Date: 2024-09-01 20:50:39 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[APIKeys](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Username] [nvarchar](255) NOT NULL,
	[APIKey] [nvarchar](195) NOT NULL,
	[ValidFrom] [date] NOT NULL,
	[ValidTo] [date] NULL,
 CONSTRAINT [PK_APIKeys] PRIMARY KEY CLUSTERED
(
	[APIKey] ASC,
	[Username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[APIKeys]  WITH CHECK ADD FOREIGN KEY([Username])
REFERENCES [dbo].[APIUsers] ([Username])
GO