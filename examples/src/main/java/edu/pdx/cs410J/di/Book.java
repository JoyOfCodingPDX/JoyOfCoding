package edu.pdx.cs410J.di;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A book in a book store
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Book
{
    private String title;

    private String author;

    private double price;

    public Book() {

    }
    
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
    public String toString()
    {
        return this.title;
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
