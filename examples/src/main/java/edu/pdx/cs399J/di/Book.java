package edu.pdx.cs399J.di;

/**
 * A book in a book store
 */
public class Book
{
    private final String title;

    private final String author;

    private final double price;

    public Book( String title, String author, double price )
    {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getTitle()
    {
        return title;
    }

    public double getPrice()
    {
        return price;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Book book = (Book) o;

        if ( author != null ? !author.equals( book.author ) : book.author != null ) return false;
        if ( title != null ? !title.equals( book.title ) : book.title != null ) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        return result;
    }
}
