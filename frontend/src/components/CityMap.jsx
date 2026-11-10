import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

// Vite doesn't resolve Leaflet's default marker URLs on its own,
// so point them at the bundled image assets explicitly.
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: markerIcon2x,
    iconUrl: markerIcon,
    shadowUrl: markerShadow,
});

function CityMap({ latitude, longitude, label }) {
    // Never hand Leaflet a missing/invalid coordinate.
    if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) return null;

    const position = [latitude, longitude];

    return (
        <div className="city-map">
            <MapContainer
                key={`${latitude},${longitude}`}
                center={position}
                zoom={10}
                scrollWheelZoom={false}
                className="city-map__container"
            >
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <Marker position={position}>
                    <Popup>{label}</Popup>
                </Marker>
            </MapContainer>
        </div>
    );
}

export default CityMap;