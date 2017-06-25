/*
 * This file is part of the IxiaS services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package ixias.play.api.auth.token

import play.api.mvc.{ RequestHeader, Result }

case class TokenViaHttpHeader(headerName: String) extends Token {
  import Token._

  /**
   * Put a specified security token to HTTP-Headers.
   */
  def put(result: Result, token: AuthenticityToken)(implicit request: RequestHeader): Result = {
    val signed = Token.signWithHMAC(token)
    result.withHeaders(headerName -> SignedToken.unwrap(signed))
  }

  /**
   * Discard a security token.
   */
  def discard(result: Result)(implicit request: RequestHeader): Result = result

  /**
   * Extract a security token from HTTP-Headers.
   */
  def extract(request: RequestHeader): Option[AuthenticityToken] =
    for {
      signed <- request.headers.get(headerName).map(SignedToken(_))
      token  <- Token.verifyHMAC(signed)
    } yield token
}
