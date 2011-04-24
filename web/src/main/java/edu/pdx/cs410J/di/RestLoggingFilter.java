package edu.pdx.cs410J.di;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Logs all calls to the rest services
 */
@Singleton
public class RestLoggingFilter implements Filter
{
    private final Logger logger;

    private final CreditCardDatabase cards;

    @Inject
    public RestLoggingFilter(Logger logger, CreditCardDatabase cards) {
      this.logger = logger;
      this.cards = cards;        
    }

    public void init( FilterConfig config ) throws ServletException
    {
      // Initialize some credit cards for testing purposes.  This probably isn't the best place to do this. 
      for (int i = 1; i <= 10; i++) {
          CreditCard card = new CreditCard(String.valueOf(i));
          cards.setBalance( card, 100.0 );
      }
    }

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        long begin = System.currentTimeMillis();

        StringBuilder description = new StringBuilder("Request from ");
        description.append(request.getRemoteAddr()).append(":").append(request.getRemotePort());

        if (request instanceof HttpServletRequest) {
          HttpServletRequest http = (HttpServletRequest) request;

          description.append(" for ").append( http.getMethod() ).append(" ").append(http.getRequestURI());
        }
        logger.info("Begin " + description);

        chain.doFilter( request, response );

        long delta = System.currentTimeMillis() - begin;

        logger.info("End (" + delta + " ms) " + description);

    }

    public void destroy()
    {

    }
}
