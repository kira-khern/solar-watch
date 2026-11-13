import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: markerIcon2x,
    iconUrl: markerIcon,
    shadowUrl: markerShadow,
});

function ClickHandler({ onMapClick }) {
    useMapEvents({
        click(e) {
            const { lat, lng } = e.latlng.wrap();
            onMapClick(lat, lng);
        },
    });
    return null;
}

function CityMap({ latitude, longitude, label, onMapClick }) {
    const hasCity = Number.isFinite(latitude) && Number.isFinite(longitude);
    const center = hasCity ? [latitude, longitude] : [30, 10];
    const zoom = hasCity ? 10 : 2;

    return (
        <div className="city-map">
            <MapContainer
                key={hasCity ? `${latitude},${longitude}` : "world"}
                center={center}
                zoom={zoom}
                minZoom={2}
                scrollWheelZoom={false}
                worldCopyJump
                className="city-map__container"
            >
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {hasCity && (
                    <Marker position={center}>
                        <Popup>{label}</Popup>
                    </Marker>
                )}
                {onMapClick && <ClickHandler onMapClick={onMapClick} />}
            </MapContainer>
        </div>
    );
}

export default CityMap;