package com.tungsten.hmclpe.sweetrice.auth.authlibinjector;

import java.io.IOException;
import java.util.Optional;

public interface AuthlibInjectorArtifactProvider {

    AuthlibInjectorArtifactInfo getArtifactInfo() throws IOException;

    Optional<AuthlibInjectorArtifactInfo> getArtifactInfoImmediately();

}