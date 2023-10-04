package com.jfecm.bankaccountmanagement.util;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;

public class Email {
	private Email() {

	}

	public static String welcomeMessage(RequestCreateClient client) {
		return
				"<html>\n" +
						"<head>\n" +
						"    <meta name=\"viewport\" content=\"user-scalable=no, width=device-width, initial-scale=1\">\n" +
						"</head>\n" +
						"<body style=\"font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;\">\n" +
						"    <div style=\"background: rgba(236, 158, 49, 0.282); margin: auto;\">\n" +
						"        <center>\n" +
						"            <img src='https://www.vhv.rs/dpng/d/438-4383567_bank-png-free-pic-transparent-bank-logo-png.png' width='25%'>\n" +
						"        </center>\n" +
						"        <center>\n" +
						"            <h1 style=\"color: #e0500e;\">WELCOME TO OUR BANK</h1>\n" +
						"            <div>\n" +
						"                <br>This email has been sent from <b>NAME_BANK</b>.<br>\n" +
						"                We welcome you to your account.<br><br>\n" +
						"                We provide you with your User and Password so that you can access our network.<br><br>\n" +
						"                <u>Client Details:</u><br><br>\n" +
						"                <table border=\"1\" style='font-size:14px; border-collapse: collapse; text-align: center; border-color:#252850;'>\n" +
						"                    <tr><td><b>User:</b></td><td>" + client.getEmail() + "</td></tr>\n" +
						"                    <tr><td><b>Password:</b></td><td>" + client.getPassword() + "</td></tr>\n" +
						"                </table><br>\n" +
						"                In case of any inconvenience, please do not hesitate to contact us at <b>jfecm.dev@gmail.com</b>.<br><br>\n" +
						"                <b><strong>Thank you for choosing us.</strong></b><br><br>\n" +
						"            </div>\n" +
						"        </center>\n" +
						"    </div>\n" +
						"</body>\n" +
						"</html>";
	}

}
