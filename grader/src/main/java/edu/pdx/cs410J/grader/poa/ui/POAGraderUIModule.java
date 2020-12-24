package edu.pdx.cs410J.grader.poa.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import edu.pdx.cs410J.grader.poa.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class POAGraderUIModule extends AbstractModule {
  @Override
  protected void configure() {

    bind(POASubmissionsView.class).to(POASubmissionsPanel.class);
    bind(POASubmissionView.class).to(POASubmissionInformationWidgets.class);
    bind(GradeBookView.class).to(GradeBookWidget.class);
    bind(UnhandledExceptionView.class).to(UnhandledExceptionDialog.class).asEagerSingleton();
    bind(StudentsView.class).to(StudentsWidget.class);
    bind(POAAssignmentsView.class).to(POAAssignmentsWidget.class);
    bind(POAGradeView.class).to(POAGradeWidgets.class);
    bind(EmailCredentialsView.class).to(EmailCredentialsDialog.class).asEagerSingleton();
    bind(StatusMessageView.class).to(StatusMessageWidget.class);

    bind(POASubmissionsPresenter.class).asEagerSingleton();
    bind(POASubmissionPresenter.class).asEagerSingleton();
    bind(GradeBookPresenter.class).asEagerSingleton();
    bind(UnhandledExceptionPresenter.class).asEagerSingleton();
    bind(StudentsPresenter.class).asEagerSingleton();
    bind(POAAssignmentsPresenter.class).asEagerSingleton();
    bind(POAGradePresenter.class).asEagerSingleton();
    bind(EmailCredentialsPresenter.class).asEagerSingleton();
    bind(StatusMessagePresenter.class).asEagerSingleton();

    bind(GradeBookFileManager.class).asEagerSingleton();
    bind(POASubmissionsDownloader.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  public EventBus provideEventBus() {
    return new EventBusThatPublishesUnhandledExceptionEvents();
  }

  @Provides
  @Singleton
  @Named("POADownloaderExecutor")
  public Executor provideDownloaderExecutor(EventBus bus) {
    return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
      @Override
      public Thread newThread(@NotNull Runnable runnable) {
        return new Thread(runnable, "POA Downloader");
      }
    }) {
      @Override
      protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?> && ((Future<?>) r).isDone()) {
          try {
            ((Future<?>) r).get();
          } catch (CancellationException ce) {
            t = ce;
          } catch (ExecutionException ee) {
            t = ee.getCause();
          } catch (InterruptedException ie) {
            // ignore/reset
            Thread.currentThread().interrupt();
          }
        }
        if (t != null) {
          bus.post(new UnhandledExceptionEvent(t));
        }
      }
    };
  }

}
