package avis.jsp.commons.file;

import avis.jsp.commons.utils.RealPathUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Avis Network on 2018-05-11.
 */
public class FileUploadService implements ServletRequest {
    
    private static final String FILEUPLOAD_TEMP_DIRECTORY = "C:/Upload/tmp/";
    
    // Required
    private final HttpServletRequest request;
    
    // Settings
    private String directory;
    private long maxSize = 1024 * 1024 * 50;
    
    // Responses
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, List<String>> parameterValues = new HashMap<>();
    private Map<String, FileHandle> handlers = new HashMap<>();
    private Map<String, File> uploadedFiles = new HashMap<>();
    
    // Encoding
    private String encodingFrom;
    private String encodingTo;
    
    public FileUploadService(final HttpServletRequest request) {
        this.request = request;
    }
    
    /**
     * Sets directory for saving file dynamically
     * This will be set under absolute path (RealPath)
     *
     * @param directory
     */
    public void setDirectory(String directory) {
        RealPathUtils.setRealPathValidated(request.getSession().getServletContext().getRealPath("/"));
        this.directory = directory;
    }
    
    /**
     * Sets maximum length of file which is being uploaded
     * 1 long = 1 byte
     *
     * @param maxSize
     */
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * Sets encodings of parameters
     *
     * @param encodingFrom
     * @param encodingTo
     */
    public void setEncoding(String encodingFrom, String encodingTo) {
        this.encodingFrom = encodingFrom;
        this.encodingTo = encodingTo;
    }
    
    /**
     * Gets parameters and files which is uploaded
     *
     * Sets {@link FileUploadService#getParameter(String)} available to use
     *
     * @return Returns a state of file uploading
     * @throws FileUploadException
     * @throws IOException
     */
    public FileUploadResult performUpload() throws FileUploadException, IOException {
        if(ServletFileUpload.isMultipartContent(request)) {
            File tempDir = new File(FILEUPLOAD_TEMP_DIRECTORY);
            if(!tempDir.exists()) {
                tempDir.mkdirs();
            }
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, tempDir)).parseRequest(request);
    
            Map<String, List<String>> parameterValues = new HashMap<>();
            Map<String, FileHandle> handlers = new HashMap<>();
    
            for(int i = 0; i < items.size(); i++) {
                FileItem item = items.get(i);
        
                if(item.isFormField()) {
                    // Text/Textarea/Select parameters
                    String value = item.getString();
                    if(encodingFrom != null && encodingTo != null) {
                        value = new String(value.getBytes(encodingFrom), encodingTo);
                    }
                    if(!parameterValues.containsKey(item.getFieldName())) {
                        parameterValues.put(item.getFieldName(), new ArrayList<>());
                    }
                    parameterValues.get(item.getFieldName()).add(value);
                } else {
                    // Binary parameters (E.g. file)
                    if(item.getFieldName() == null || item.getFieldName().isEmpty()) {
                        return FileUploadResult.NON_NAME;
                    }
            
                    if(item.getSize() > maxSize) {
                        return FileUploadResult.EXCEED_SIZE_LIMIT;
                    }
            
                    FileHandle handle = new FileHandle();
                    String[] nameSplit = item.getName().split("\\\\");
                    handle.fileName = nameSplit[nameSplit.length - 1];
                    handle.inputStream = item.getInputStream();
                    
                    System.out.println("File Input: " + item.getFieldName() + " -> " + handle.fileName);
                    
                    handlers.put(item.getFieldName(), handle);
                }
            }
    
            this.parameterValues = parameterValues;
            for(Map.Entry<String, List<String>> entry : parameterValues.entrySet()) {
                this.parameters.put(entry.getKey(), entry.getValue().iterator().next());
            }
            this.handlers = handlers;
            return FileUploadResult.SUCCESS;
        } else {
            return FileUploadResult.NON_MULTIPART;
        }
    }
    
    /**
     *
     * Exports binary data which is loaded from InputStream as a file
     *
     * Sets {@link FileUploadService#getFileContent(String)} available to use.
     *
     * @throws IOException
     */
    public void exportFiles() throws IOException {
        String imPath = RealPathUtils.getRealPath();
        if(directory.startsWith("/")) {
            imPath += directory;
        } else {
            imPath += "/" + directory;
        }
        File directory = new File(imPath);
        if(!directory.exists()) {
            directory.mkdirs();
        }
        
        this.uploadedFiles = uploadFile(imPath);
    }
    
    private Map<String, File> uploadFile(String imPath) throws IOException {
        Map<String, File> uploadedFiles = new HashMap<>();
        for(Map.Entry<String, FileHandle> entry : handlers.entrySet()) {
            FileHandle handle = entry.getValue();
        
            try(OutputStream outputStream = new FileOutputStream(imPath + handle.fileName)) {
                int read = 0;
                byte[] bytes = new byte[1024];
                while((read = handle.inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            
                uploadedFiles.put(entry.getKey(), new File(imPath + handle.fileName));
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                if(handle.inputStream != null) {
                    handle.inputStream.close();
                }
            }
        }
        return uploadedFiles;
    }
    
    @Override
    public Object getAttribute(String s) {
        return request.getAttribute(s);
    }
    
    @Override
    public Enumeration<String> getAttributeNames() {
        return request.getAttributeNames();
    }
    
    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }
    
    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        request.setCharacterEncoding(s);
    }
    
    @Override
    public int getContentLength() {
        return request.getContentLength();
    }
    
    @Override
    public String getContentType() {
        return request.getContentType();
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return request.getInputStream();
    }
    
    public String getParameter(String key) {
        return parameters.get(key);
    }
    
    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> vector = new Vector<>(parameters.keySet());
        return vector.elements();
    }
    
    public String[] getParameterValues(String key) {
        if(parameterValues.get(key) == null) {
            return new String[0];
        }
        return parameterValues.get(key).toArray(new String[0]);
    }
    
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<>();
        for(Map.Entry<String, List<String>> entry : parameterValues.entrySet()) {
            parameterMap.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
        return parameterMap;
    }
    
    @Override
    public String getProtocol() {
        return request.getProtocol();
    }
    
    @Override
    public String getScheme() {
        return request.getScheme();
    }
    
    @Override
    public String getServerName() {
        return request.getServerName();
    }
    
    @Override
    public int getServerPort() {
        return request.getServerPort();
    }
    
    @Override
    public BufferedReader getReader() throws IOException {
        return request.getReader();
    }
    
    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }
    
    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }
    
    @Override
    public void setAttribute(String s, Object o) {
        request.setAttribute(s, o);
    }
    
    @Override
    public void removeAttribute(String s) {
        request.removeAttribute(s);
    }
    
    @Override
    public Locale getLocale() {
        return request.getLocale();
    }
    
    @Override
    public Enumeration<Locale> getLocales() {
        return request.getLocales();
    }
    
    @Override
    public boolean isSecure() {
        return request.isSecure();
    }
    
    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return request.getRequestDispatcher(s);
    }
    
    @Override @Deprecated
    public String getRealPath(String s) {
        return request.getRealPath(s);
    }
    
    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }
    
    @Override
    public String getLocalName() {
        return request.getLocalName();
    }
    
    @Override
    public String getLocalAddr() {
        return request.getLocalAddr();
    }
    
    @Override
    public int getLocalPort() {
        return request.getLocalPort();
    }
    
    @Override
    public ServletContext getServletContext() {
        return request.getServletContext();
    }
    
    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return request.startAsync();
    }
    
    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return request.startAsync(servletRequest, servletResponse);
    }
    
    @Override
    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }
    
    @Override
    public boolean isAsyncSupported() {
        return request.isAsyncSupported();
    }
    
    @Override
    public AsyncContext getAsyncContext() {
        return request.getAsyncContext();
    }
    
    @Override
    public DispatcherType getDispatcherType() {
        return request.getDispatcherType();
    }
    
    public File getFileContent(String key) {
        return uploadedFiles.get(key);
    }
    
    public String getFileName(String key) {
        File file = getFileContent(key);
        return file != null ? file.getName() : null;
    }
    
    public String getDirectory() {
        RealPathUtils.setRealPathValidated(request.getSession().getServletContext().getRealPath("/"));
        return directory;
    }
    
    public long getMaxSize() {
        return maxSize;
    }
    
    public void deleteFiles() {
        for(File file : uploadedFiles.values()) {
            file.delete();
        }
        uploadedFiles.clear();
    }
    
    public HttpServletRequest getRequest() {
        return request;
    }
}
