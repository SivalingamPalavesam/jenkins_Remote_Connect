package com.jenkins.serverstart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CmdRun {
	@GetMapping("/Start")
	public String cmdLine(String[] args) {
		String[] command = { "cmd", };
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			new Thread(new WriteCmd(p.getErrorStream(), System.err)).start();
			new Thread(new WriteCmd(p.getInputStream(), System.out)).start();
			PrintWriter stdin = new PrintWriter(p.getOutputStream());
			stdin.println("D:");
			stdin.println("cd jenkins");
			stdin.println("java -jar jenkins.war");
			stdin.close();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "buildStatus";
	}
	String pidnumber;

	@GetMapping("/Stop")
	public String pid(String[] args) {

		try {
			Process p = Runtime.getRuntime().exec("cmd /c netstat -ano | findstr :8080");
			String s;
			System.out.println(p.getOutputStream());
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				String[] temp = s.split("LISTENING");
				pidnumber = temp[1];
				System.out.println("name" + pidnumber);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return StopServer(args);
	}

	public String StopServer(String[] args) {
		String[] command = { "cmd", };
		Process pro;
		try {

			pro = Runtime.getRuntime().exec(command);
			new Thread(new WriteCmd(pro.getErrorStream(), System.err)).start();
			new Thread(new WriteCmd(pro.getInputStream(), System.out)).start();
			PrintWriter stdin = new PrintWriter(pro.getOutputStream());
			stdin.println("D:");
			stdin.println("taskkill /PID "+pidnumber+" /F");
			stdin.close();
			pro.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "buildStatus";

	}
}
