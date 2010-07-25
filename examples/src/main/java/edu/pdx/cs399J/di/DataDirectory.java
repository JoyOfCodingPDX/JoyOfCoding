package edu.pdx.cs399J.di;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.*;

/**
 * A marker interface for constructor parameters whose values should be the data directory
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.PARAMETER, ElementType.FIELD })
@Documented
public @interface DataDirectory
{
}
