package com.example.soilrespiration.common;

public interface ResponseHandler {

    /**
     *  上传任务执行成功
     * @param response
     */
    void success(CommonResponse response);

    /**
     *  上传任务执行失败
     * @param failCode 返回的任务状态码
     * @param failMsg 返回的失败原因
     */
    void fail(String failCode, String failMsg);
}
