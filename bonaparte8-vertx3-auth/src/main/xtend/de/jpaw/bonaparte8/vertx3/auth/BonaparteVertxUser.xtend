package de.jpaw.bonaparte8.vertx3.auth

import de.jpaw.bonaparte.pojos.api.auth.JwtInfo
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User

public class BonaparteVertxUser implements User {
    private final String jwtToken;          // encoded form, without "Bearer" prefix
    private final JwtInfo info;             // decoded data in map form
    private final JsonObject principal;     // token + decoded map

    public new (String jwtToken, JwtInfo info) {
        this.jwtToken = jwtToken;
        this.info = info
        principal = new JsonObject(#{ "jwt" -> jwtToken, "info" -> info })
    }
    override clearCache() {
    }

    override principal() {
        return principal;
    }

    override isAuthorized(String authority, Handler<AsyncResult<Boolean>> resultHandler) {
        throw new UnsupportedOperationException("TODO: auto-generated method stub")
    }

    override setAuthProvider(AuthProvider authProvider) {
        throw new UnsupportedOperationException("TODO: auto-generated method stub")
    }
}
