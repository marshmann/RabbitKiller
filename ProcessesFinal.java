//Author: Nicholas Marshman
//A program that will stop a rabbit from executing on your computer, as long as this process is running.

//Currently whitelists processes on your computer on start up, TODO: Add a way to whitelist other processes.

import java.lang.ProcessHandle;
import java.util.ArrayList;
import java.lang.Thread;

public class ProcessesFinal {
    
    static ArrayList<Long> whitelist = new ArrayList<Long>();
    static boolean cmdFlag = false;
    static boolean cmdFlag2 = false;
    static boolean iniFlag = false;
    static ProcessHandle badpid = null;
    static long startTime, delayTime = 5000000000L, currentTime;
    
    public static void main(String[] args) throws InterruptedException {            
        //Infinite Loop so it never stops running until we close the window
        while(true){
            if(iniFlag == false){    
                ProcessHandle.allProcesses().
                forEach(process -> {
                    try {addWhitelist(process);} 
                    catch (InterruptedException e) {e.printStackTrace();} 
                });
                iniFlag = true;    
                
                if(badpid != null && badpid.info().command().toString().contains("cmd.exe")) {
                    startTime = System.nanoTime();
                }
            }
            else{                
                ProcessHandle.allProcesses().forEach(process -> {
                    if((System.nanoTime() - startTime) >= delayTime && badpid != null)
                        badpid.destroyForcibly();
                    if(!whitelist.contains(process.pid())){
                        System.out.println("Destroyed - PID: "+ process +
                            " Path Name: " + process.info().command());
                        process.destroyForcibly();                        
                    }
                });
            }
        }
    }
    //Add processes to a whitelist
    private static void addWhitelist(ProcessHandle process) throws InterruptedException {
        String s = process.info().command().toString();
        long pid = process.pid();
        //Add our program's cmd window to the whitelist
        if(cmdFlag == false &&
            (s.contains("C:\\Windows\\System32\\cmd.exe") || s.contains("terminal")))
        {
            whitelist.add(pid);
            cmdFlag = true;
            System.out.println(s + " heee1");
            process.destroy(); //If on linux, will kill all terminals
                               //Including the one running the program
        }
        else if(cmdFlag2 == false && cmdFlag == true && 
            (s.contains("C:\\Windows\\System32\\cmd.exe") || s.contains("terminal")))
        {
            badpid = process;
            whitelist.add(pid);
            cmdFlag2 = true;
            process.destroy();
        }
        else if(!s.contains("C:\\Windows\\System32\\cmd.exe") || !s.contains("terminal")){
            whitelist.add(pid);
            if(s.replaceAll("Optional(.empty)?","").compareTo("")!=0)
                System.out.println(s);
        }
    }
}
