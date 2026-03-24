package com.oz.domain.exception;

public class RegraNegocioException extends Exception {
    public RegraNegocioException() {
        super();
    }

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }

    public RegraNegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

