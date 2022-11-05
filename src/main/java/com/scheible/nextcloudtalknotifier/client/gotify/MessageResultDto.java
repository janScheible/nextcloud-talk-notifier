package com.scheible.nextcloudtalknotifier.client.gotify;

/**
 *
 * @author sj
 */
public record MessageResultDto(int id, int appid, String title, String message, int priority, String date) {

}
