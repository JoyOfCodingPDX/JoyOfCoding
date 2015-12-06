package edu.pdx.cs410J.original.client;

import edu.pdx.cs410J.AbstractPhoneCall;

import java.lang.Override;
import java.util.Date;

public class PhoneCall extends AbstractPhoneCall
{

  @Override
  public String getCaller() {
    return "123-345-6789";
  }

  @Override
  public Date getStartTime() {
    return new Date();
  }

  public String getStartTimeString() {
    return "START " + getStartTime();
  }

  @Override
  public String getCallee() {
    return "345-677-2341";
  }

  public Date getEndTime() {
    return new Date();
  }

  public String getEndTimeString() {
    return "END " + getEndTime();
  }

}
