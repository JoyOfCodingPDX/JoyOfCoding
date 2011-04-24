package edu.pdx.cs410J.di;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.*;

/**
 * A marker interface for constructor parameters whose values should be the data directory
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD })
@Documented
public @interface DataDirectory
{
}
