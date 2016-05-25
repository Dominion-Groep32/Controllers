package Controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.*;
import engine.*;

public class DominionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DominionServlet() {
		super();
	}

	private void kaartKopen(HttpServletRequest request, SpelEngine engine) throws ServletException, IOException {
		Kaart keuze = geefGespeeldeKaart(request.getParameter("kaart"), engine);
		if (keuze.geefKost() <= engine.geefHuidigeSpeler().geefGeld() && engine.geefHuidigeSpeler().geefAankoop() > 0) {
			engine.brengGekochteKaartNaarAflegstapel(keuze);
			engine.geefHuidigeSpeler().verminderGeld(keuze.geefKost());
			engine.geefHuidigeSpeler().verminderAankoop(1);
		}
	}

	private void spelersToevoegen(HttpServletRequest request, SpelEngine engine)
			throws ServletException, IOException {
		String spelers = request.getParameter("spelers");
		String[] splitNames = spelers.split(",");
		engine.maakSpelersAan(splitNames);
	}

	private void geefKaartenInHandVanDeHuidigeSpeler(HttpServletRequest request, HttpServletResponse response,
			SpelEngine engine) throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
		for (int i = 0; i < engine.geefHuidigeSpeler().geefKaartenInHand().size(); i++) {
			arrayObj.put(i, engine.geefHuidigeSpeler().geefKaartenInHand().get(i).geefNaam());
		}
		response.getWriter().write(arrayObj.toString());
	}

	private void genereerActieKaart(HttpServletRequest request, HttpServletResponse response, SpelEngine engine)
			throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
		for (int i = 0; i < 10; i++) {
			arrayObj.put(i, engine.geefLijst10GekozenActiekaarten().get(i).geefNaam());
		}
		response.getWriter().write(arrayObj.toString());
	}

	private void geefInfoOverDeKaart(HttpServletRequest request, HttpServletResponse response, SpelEngine engine)
			throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
		String info = geefGespeeldeKaart(request.getParameter("kaart"), engine).geefInfo();
		arrayObj.put(0, info);
		response.getWriter().write(arrayObj.toString());
	}

	private void geefHuidigeWaardenVanDeSpeler(HttpServletRequest request, HttpServletResponse response,
			SpelEngine engine) throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
		
		arrayObj.put(0, "Nu aan de beurt: " + engine.geefHuidigeSpeler().geefNaam());
		arrayObj.put(1, "Geld: " + engine.geefHuidigeSpeler().geefGeld());
		arrayObj.put(2, "Aankoop: " + engine.geefHuidigeSpeler().geefAankoop());
		arrayObj.put(3, "Acties: " + engine.geefHuidigeSpeler().geefActie());
		response.getWriter().write(arrayObj.toString());
	}

	private void geefHuidigSpeelVeld(HttpServletRequest request, HttpServletResponse response, SpelEngine engine)
			throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
		
		for (int i = 0; i < engine.geefHuidigeSpeler().geefSpeelGebied().size(); i++) {
			
			arrayObj.put(i, engine.geefHuidigeSpeler().geefSpeelGebied().get(i).geefNaam());
		}
		
		response.getWriter().write(arrayObj.toString());
	}
	private void toonVorigeActies(HttpServletRequest request, HttpServletResponse response, SpelEngine engine)throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
		
		engine.LijstAndereSpelersMaken(engine.geefHuidigeSpeler());
		Speler volgendespeler = engine.geefLijstAlleSpelers().get(1);
		
		arrayObj.put(0,volgendespeler.geefNaam()+ " het is nu jouw beurt. Hieronder zie je wat "+engine.geefHuidigeSpeler().geefNaam() +" gedaan heeft.");
		
		for (int i = 1; i <= engine.geefHuidigeSpeler().geefSpeelGebied().size(); i++) {
			
			arrayObj.put(i, engine.geefHuidigeSpeler().geefSpeelGebied().get(i-1).geefNaam());
		}
		
		response.getWriter().write(arrayObj.toString());
	}

	private void kaartUitvoeren(HttpServletRequest request, HttpServletResponse response, SpelEngine engine)throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
		Kaart result = geefGespeeldeKaart(request.getParameter("kaart"), engine);
		
		if (result.geefKaartType().equals("geldkaart")) {
			engine.geefHuidigeSpeler().vermeerderGeld(result.geefWaarde());
			engine.brengEenKaartVanDeEneNaarAndereStapel(engine.geefHuidigeSpeler().geefKaartenInHand(), result,
			engine.geefHuidigeSpeler().geefSpeelGebied());
		}

		else if (result.geefKaartType().equals("actiekaart") && engine.geefHuidigeSpeler().geefActie() >= 1) {
			ExtraInfo kaart = engine.actieUitvoeren(result);
			switch (result.geefNaam()) {
			case "kapel":
				engine.zetHuidigeKaart(result);
				arrayObj.put(0, "speciaal");
				arrayObj.put(1, kaart.geefMaxAantalKaarten());
				arrayObj.put(2, kaart.geefBericht());
				arrayObj.put(3, "huidigeSpeler");
				response.getWriter().write(arrayObj.toString());
				engine.zetAantalGekozenKaarten(kaart.geefMaxAantalKaarten());

				break;

			default:
				break;

			}
		}
	}

	private Kaart geefGespeeldeKaart(String gekozenKaart, SpelEngine engine) {

		for (int i = 0; i < engine.geefLijstKaartenVanHetSpel().size(); i++) {
			if (gekozenKaart.equals(engine.geefLijstKaartenVanHetSpel().get(i).geefNaam())) {
				return engine.geefLijstKaartenVanHetSpel().get(i);
			}
		}
		return null;
	}

	private void geefAantalKaartenBinnenDeStapels(HttpServletRequest request, HttpServletResponse response,
			SpelEngine engine) throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
		for (int i = 0; i < engine.geefLijstKaartenVanHetSpel().size(); i++) {
			arrayObj.put(i, engine.geefLijstStapels().get(i).geefAatalResterendeKaartenInDeStapel());
		}
		response.getWriter().write(arrayObj.toString());
	}

	private void geefKaartenDieJeKuntKopen(HttpServletRequest request, HttpServletResponse response,
			SpelEngine engine) throws ServletException, IOException {

		JSONArray arrayObj = new JSONArray();
		if (engine.geefHuidigeSpeler().geefAankoop() > 0) {
			for (int i = 0; i < engine.geefLijstKaartenDieJeKuntKopen().size(); i++) {
				arrayObj.put(i, engine.geefLijstKaartenDieJeKuntKopen().get(i).geefNaam());
			}
		}
		response.getWriter().write(arrayObj.toString());

	}

	private void specialeKaartenInHand(HttpServletRequest request, HttpServletResponse response, SpelEngine engine)
			throws ServletException, IOException {

		JSONArray arrayObj = new JSONArray();
		for (int i = 0; i < engine.geefHuidigeSpeler().geefKaartenInHand().size(); i++) {
			arrayObj.put(i, engine.geefHuidigeSpeler().geefKaartenInHand().get(i).geefNaam());
		}
		response.getWriter().write(arrayObj.toString());
	}

	private void tweedeActieProberen(HttpServletRequest request, HttpServletResponse response, SpelEngine engine)
			throws ServletException, IOException {

		Kaart result = geefGespeeldeKaart(request.getParameter("kaart"), engine);
		switch (engine.krijgHuidigeKaart().geefNaam()) {
		case "kapel":
			if (engine.krijgAantalGekozenKaarten() > 0) {
				for (int j = 0; j < engine.geefHuidigeSpeler().geefKaartenInHand().size(); j++) {
					if (result.geefNaam() == engine.geefHuidigeSpeler().geefKaartenInHand().get(j).geefNaam()) {
						engine.brengEenKaartVanDeEneNaarAndereStapel(engine.geefHuidigeSpeler().geefKaartenInHand(), engine.geefHuidigeSpeler().geefKaartenInHand().get(j), engine.geefHuidigeSpeler().geefVuilbakStapel());
						
						engine.zetAantalGekozenKaarten(engine.krijgAantalGekozenKaarten() - 1);
						break;
					}
				}
			}
			break;

		default:
			break;
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");

		SpelEngine gameEngine = (SpelEngine) request.getServletContext().getAttribute("SpelFuncties");
		if (gameEngine == null) {
			gameEngine = new SpelEngine();
			request.getServletContext().setAttribute("SpelFuncties", gameEngine);
		}

		switch (request.getParameter("operation")) {
		case "spelerToevoegen":
			spelersToevoegen(request, gameEngine);
			break;
		case "geefKaartenInHand":
			geefKaartenInHandVanDeHuidigeSpeler(request, response, gameEngine);
			break;
		case "stapelsGeneren":
			geefAantalKaartenBinnenDeStapels(request, response, gameEngine);
			break;
		case "actieKaartenGeneren":
			genereerActieKaart(request, response, gameEngine);
			break;
		case "stopBeurt":
			gameEngine.brengAlleKaartenNaarAflegstapel();
			gameEngine.geefHuidigeSpeler().herstelWaarden();
			gameEngine.trekKaartVanTrekStapel(gameEngine.geefHuidigeSpeler(), 5);
			gameEngine.volgendeSpeler();
			break;
		case "kaartenKopen":
			kaartKopen(request, gameEngine);
			break;
		case "huidigeWaarden":
			geefHuidigeWaardenVanDeSpeler(request, response, gameEngine);
			break;
		case "infoOphalen":
			geefInfoOverDeKaart(request, response, gameEngine);
			break;
		case "brengGeldKaartenUitHandaarSpeelVeld":
			for (int i = 0; i < gameEngine.geefHuidigeSpeler().geefKaartenInHand().size(); i++) {
				if(gameEngine.geefHuidigeSpeler().geefKaartenInHand().get(i).geefKaartType()=="geldkaart"){
					gameEngine.geefHuidigeSpeler().vermeerderGeld(gameEngine.geefHuidigeSpeler().geefKaartenInHand().get(i).geefWaarde());
				}
				
			}
			gameEngine.brengAlleGeldkaartenUitHandNaarStapel(gameEngine.geefHuidigeSpeler().geefSpeelGebied());
			
			break;
		case "toonSpeelVeld":
			geefHuidigSpeelVeld(request, response, gameEngine);
			break;
		case "trekKaartInHand":
			gameEngine.trekKaartVanTrekStapel(gameEngine.geefHuidigeSpeler(), 5);
			break;
		case "actieUitvoeren":
			kaartUitvoeren(request, response, gameEngine);
			break;
		case "KaartenDieJeKuntKopen":
			geefKaartenDieJeKuntKopen(request, response, gameEngine);
			break;
		case "special":
			specialeKaartenInHand(request, response, gameEngine);
			break;
		case "tweedeActieUitvoeren":
			tweedeActieProberen(request, response, gameEngine);
			break;
		case "vorigeActies":
			toonVorigeActies(request,response,gameEngine);

		default:
			break;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().append("hello world vanuit post");
	}
}