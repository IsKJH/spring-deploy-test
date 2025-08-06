package com.example.demo.company.controller.response_form;

import com.example.demo.company.service.response.CompanyRegisterResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompanyRegisterResponseForm {
    private final String name;

    public static CompanyRegisterResponseForm from(final CompanyRegisterResponse response) {
        return new CompanyRegisterResponseForm(
                response.getName()
        );
    }

}
