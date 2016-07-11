package au.com.javacloud.lxd.model;

import java.util.Date;
import java.util.Properties;

/**
 * Created by david on 11/07/16.
 */
public class Image {
    private String alias;
    private String fingerprint;
    private boolean publicImage;
    private String description;
    private String architecture;
    private String size;
    private Date createdDate;
    private Date uploadedDate;
    private Date expiresDate;
    private Properties properties;
    private boolean autoupdate;

    public static final String DELIM = "\\|";
    public static final String DATEFORMAT = "xxx"; // TODO

    public String toString() {
        return "alias="+alias+" fingerprint="+fingerprint+" public="+publicImage+" desc="+description+" arch="+architecture+" size="+size+" uploadedDate="+uploadedDate;
    }
    public static Image parse(String input) throws Exception {
        if (input==null || input.trim().length()==0) {
            return null;
        }
        String[] imageParts = input.split(DELIM);
        if (imageParts.length!=8) {
            throw new Exception("Expected 8 columns, named ALIAS, FINGERPRINT, DESC, PUBLIC, " +
                    "ARCH, SIZE, UPLOAD DATE. Got "+imageParts.length);
        }
        Image image = new Image();
        for (int i=0; i<imageParts.length; i++) {
            String imagePart = imageParts[i].trim();
            // Header column
            if (imagePart.contains("ALIAS")) {
                return null;
            }
            switch (i) {
                case 1: image.alias = imagePart; break;
                case 2: image.fingerprint= imagePart; break;
                case 3: image.publicImage = Boolean.parseBoolean(imagePart); break;
                case 4: image.description = imagePart; break;
                case 5: image.architecture = imagePart; break;
                case 6: image.size = imagePart; break;
                //case 7: image.uploadedDate = Date.parse(imagePart); break; // TODO
            }
        }
        return image;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public boolean isPublicImage() {
        return publicImage;
    }

    public void setPublicImage(boolean publicImage) {
        this.publicImage = publicImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public Date getExpiresDate() {
        return expiresDate;
    }

    public void setExpiresDate(Date expiresDate) {
        this.expiresDate = expiresDate;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean isAutoupdate() {
        return autoupdate;
    }

    public void setAutoupdate(boolean autoupdate) {
        this.autoupdate = autoupdate;
    }
}
