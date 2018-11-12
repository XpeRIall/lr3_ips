package controllers

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.test._
import org.specs2.mock.Mockito
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

/**
  * Test case for the [[controllers.ApplicationController]] class.
  */
class ApplicationControllerSpec extends PlaySpecification with Mockito {
  sequential

  "The `index` action" should {
    "redirect to login page if user is unauthorized" in new TestContext {
      new WithApplication(application) {
        val Some(redirectResult) = route(FakeRequest(routes.ApplicationController index())
          .withAuthenticator[CookieAuthenticator](LoginInfo("invalid", "invalid")))

        status(redirectResult) must be equalTo SEE_OTHER

        val redirectURL: String = redirectLocation(redirectResult) getOrElse ""
        redirectURL must contain((routes.ApplicationController signIn()).toString)

        val Some(unauthorizedResult) = route(FakeRequest(GET, redirectURL))

        status(unauthorizedResult) must be equalTo OK
        contentType(unauthorizedResult) must beSome("text/html")
        contentAsString(unauthorizedResult) must contain("Silhouette - Sign In")
      }
    }

    "return 200 if user is authorized" in new TestContext {
      new WithApplication(application) {
        val Some(result) = route(FakeRequest(routes.ApplicationController index()) withAuthenticator[CookieAuthenticator] identity.loginInfo)

        status(result) must beEqualTo(OK)
      }
    }
  }
}