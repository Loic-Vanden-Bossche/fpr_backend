package com.esgi.domainmodels.exceptions

class EmailAlreadyExistsException(email: String) : DomainException("Email $email already exists")