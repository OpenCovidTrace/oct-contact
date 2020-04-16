import filters.AccessLoggingFilter
import javax.inject.Inject
import play.api.http.{DefaultHttpFilters, EnabledFilters}

class Filters @Inject()(defaultFilters: EnabledFilters, accessLoggingFilter: AccessLoggingFilter)
  extends DefaultHttpFilters(defaultFilters.filters :+ accessLoggingFilter: _*)
