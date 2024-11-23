package com.software.software_project_sem4.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class CustomErrResp {
    private Map<String, List<String>> errors;
}
