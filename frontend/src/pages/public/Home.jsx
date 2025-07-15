import Footer from "../../components/common/Footer";
import Navbar from "../../components/common/Navbar";
import SearchCard from "../../components/common/SearchCard";
import Destinations from "./Destinations";

function Home() {
  return (
    <div>
      <Navbar />
      <SearchCard />
      <Destinations />
      <Footer />
    </div>
  );
}
export default Home;
