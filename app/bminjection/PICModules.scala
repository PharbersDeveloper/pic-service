package bminjection

import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmutil.logging.PharbersLog
import play.api.{Configuration, Environment}

class PICModules extends play.api.inject.Module {
    def bindings(env : Environment, conf : Configuration) = {
        Seq(
            bind[DBTrait].to[PICModuleImpl],
            bind[AuthTokenTrait].to[PICModuleImpl],
            bind[PharbersLog].to[PICModuleImpl]
        )
    }
}