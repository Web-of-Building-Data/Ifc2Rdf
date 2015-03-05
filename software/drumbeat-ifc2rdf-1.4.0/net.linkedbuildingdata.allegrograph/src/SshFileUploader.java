import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.linkedbuildingdata.common.string.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


public class SshFileUploader {

	/**
	 * 
	 * @param sourceFilePath
	 * @param destinationFilePath
	 * @param destinationHost
	 * @param destinationPort
	 * @param destinationUserName
	 * @param destinationUserPassword
	 * @param destinationPrivateKeyFilePath
	 * @param destinationPrivateKeyPassphrase
	 * @throws IOException
	 * @throws JSchException
	 * @throws SftpException
	 */
	public static void copyFile(
			String sourceFilePath,
			String destinationFilePath,
			String destinationHost,
			String destinationPort,
			String destinationUserName,
			String destinationUserPassword,
			String destinationPrivateKeyFilePath,
			String destinationPrivateKeyPassphrase) throws IOException, JSchException, SftpException {
		
		JSch jsch = new JSch();
		
		if (!StringUtils.isEmptyOrNull(destinationPrivateKeyFilePath)) {
			jsch.addIdentity(destinationPrivateKeyFilePath, destinationPrivateKeyPassphrase);
		}
		
		int destinationPortNumber = 22;
		if (!StringUtils.isEmptyOrNull(destinationPort)) {
			destinationPortNumber = Integer.parseInt(destinationPort);
		}
		
		Session session = jsch.getSession(destinationUserName, destinationHost, destinationPortNumber);
		if (!StringUtils.isEmptyOrNull(destinationUserPassword)) {
			session.setPassword(destinationUserPassword);
		}
		
//		Properties config = new Properties();
//        config.put("StrictHostKeyChecking", "no");
//        session.setConfig(config);
        session.connect();
        
        Channel channel = session.openChannel("sftp");
        channel.connect();
        
        ChannelSftp channelSftp = (ChannelSftp)channel;        
        File destinationFile = new File(destinationFilePath);
        channelSftp.cd(destinationFile.getPath());
        channelSftp.put(new FileInputStream(sourceFilePath), destinationFile.getName());
		
		
	}

}
