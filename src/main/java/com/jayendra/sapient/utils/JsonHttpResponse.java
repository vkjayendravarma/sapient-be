package com.jayendra.sapient.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class JsonHttpResponse {
    public Object data;
    public int status;
    public boolean success;
}
