package com.plaiaundi.sepe.seid.dto;

public class OpenDataCamera {
    
    // Atributos
    private String address;
    private String cameraId; // obligatorio
    private String cameraName;
    private String kilometer;
    private String latitude;
    private String longitude;
    private String road;
    private String sourceId; // obligatorio
    private String urlImage;

    // Constructor
    public OpenDataCamera(String address, String cameraId, String cameraName, String kilometer, String latitude,
            String longitude, String road, String sourceId, String urlImage) {
        this.address = address;
        this.cameraId = cameraId;
        this.cameraName = cameraName;
        this.kilometer = kilometer;
        this.latitude = latitude;
        this.longitude = longitude;
        this.road = road;
        this.sourceId = sourceId;
        this.urlImage = urlImage;
    }

    // Getter y setters
    public String getAddress()                      { return address;               }
    public void setAddress(String address)          { this.address = address;       }

    public String getCameraId()                     { return cameraId;              }
    public void setCameraId(String cameraId)        { this.cameraId = cameraId;     }

    public String getCameraName()                   { return cameraName;            }
    public void setCameraName(String cameraName)    { this.cameraName = cameraName; }

    public String getKilometer()                    { return kilometer;             }
    public void setKilometer(String kilometer)      { this.kilometer = kilometer;   }

    public String getLatitude()                     { return latitude;              }
    public void setLatitude(String latitude)        { this.latitude = latitude;     }

    public String getLongitude()                    { return longitude;             }
    public void setLongitude(String longitude)      { this.longitude = longitude;   }

    public String getRoad()                         { return road;                  }
    public void setRoad(String road)                { this.road = road;             }

    public String getSourceId()                     { return sourceId;              }
    public void setSourceId(String sourceId)        { this.sourceId = sourceId;     }

    public String getUrlImage()                     { return urlImage;              }
    public void setUrlImage(String urlImage)        { this.urlImage = urlImage;     }   

}
