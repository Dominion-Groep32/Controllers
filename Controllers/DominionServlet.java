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
    private void kopen(HttpServletRequest request, HttpServletResponse response,SpelFuncties engine) throws ServletException, IOException {
    	JSONObject jsonObj = new JSONObject();
    	String gekozenKaart = request.getParameter("kaart");
    	Kaart keuze = geefGespeeldKaart(gekozenKaart, engine);
    	
    	if(keuze.geefKost()<= engine.geefHuidigeSpeler().geefGeld())
    	{
    		engine.brengGekochteKaartNaarAflegstapel(keuze);
    		engine.geefHuidigeSpeler().verminderGeld(keuze.geefKost());
    		engine.geefHuidigeSpeler().verminderAankoop(1);
    		jsonObj.put("naam", keuze.geefNaam());
			jsonObj.put("kost", keuze.geefKost());
    	}
		response.getWriter().write(jsonObj.toString());
	
		
    }
    
  
    private void spelersToevoegen(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {	
    	JSONObject jsonObj = new JSONObject();
    	String spelerNaam = request.getParameter("speler1");
    	String spelerNaam2 = request.getParameter("speler2");
    	String spelers[] = {spelerNaam, spelerNaam2};
    	engine.maakSpelersAan(spelers);
		jsonObj.put("Speler1", spelers[0]);
		jsonObj.put("Speler2", spelers[1]);
		response.getWriter().write(jsonObj.toString());
		
    }
	
    private void geefKaartenInHandVanDeHuidigeSpeler(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {
    	JSONArray arrayObj = new JSONArray();
		for(int i=0; i<engine.geefHuidigeSpeler().geefKaartenInHand().size();i++){arrayObj.put(i, engine.geefHuidigeSpeler().geefKaartenInHand().get(i).geefNaam());}
		response.getWriter().write(arrayObj.toString());
    }

    private void genereerActieKaart(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {
    	
    	JSONArray arrayObj = new JSONArray();
		for(int i=0; i <10;i++){arrayObj.put(i, engine.geefLijst10GekozenActiekaarten().get(i).geefNaam());}
		response.getWriter().write(arrayObj.toString());
    }
   

	 private void geefInfoOverDeKaart(HttpServletRequest request, HttpServletResponse response,SpelFuncties engine) throws ServletException, IOException {
		JSONArray arrayObj = new JSONArray();
    	String gekozenKaart = request.getParameter("kaart");
    	String info = geefGespeeldKaart(gekozenKaart,engine).geefInfo();
    	arrayObj.put(0,info);
    	response.getWriter().write(arrayObj.toString());
    	
    	
    }
	 private void huidigeWaarden(HttpServletRequest request, HttpServletResponse response,SpelFuncties engine) throws ServletException, IOException {
	    	JSONArray arrayObj = new JSONArray();
	    	arrayObj.put(0,"Speler: "+engine.geefHuidigeSpeler().geefNaam());
	    	arrayObj.put(1,"Geld: "+engine.geefHuidigeSpeler().geefGeld());
	    	arrayObj.put(2,"Aankoop: "+engine.geefHuidigeSpeler().geefAankoop());
	    	arrayObj.put(3,"Acties: "+engine.geefHuidigeSpeler().geefActie());
	    	response.getWriter().write(arrayObj.toString());
	    }
	 private void geefHuidigSpeelVeld(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {
	    	JSONArray arrayObj = new JSONArray();
			for(int i=0; i< engine.geefHuidigeSpeler().geefSpeelGebied().size();i++){arrayObj.put(i, engine.geefHuidigeSpeler().geefSpeelGebied().get(i).geefNaam());}
			engine.geldOpSpeelVeld();
			response.getWriter().write(arrayObj.toString());		
	    }
	 private void kaartSpelen(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {
		 	String gekozenKaart = request.getParameter("kaart");
	    	Kaart result = geefGespeeldKaart(gekozenKaart, engine);
	    	if (result.geefKaartType().equals("actiekaart")){
	    		engine.actieUitvoeren(result);
	    	}
	    	
    		engine.brengEenKaartVanDeEneNaarAndereStapel(engine.geefHuidigeSpeler().geefKaartenInHand(), result, engine.geefHuidigeSpeler().geefSpeelGebied());
	 }
	 
	 private Kaart geefGespeeldKaart (String gekozenKaart,SpelFuncties engine){
		
		 for (int i = 0; i < engine.geefLijstKaartenVanHetSpel().size() ; i++) {
			 if(gekozenKaart.equals(engine.geefLijstKaartenVanHetSpel().get(i).geefNaam()))
				 {
				 return engine.geefLijstKaartenVanHetSpel().get(i);
				 }
		 }
		 
		 return null;
	 }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		SpelFuncties gameEngine = (SpelFuncties) request.getServletContext().getAttribute("SpelFuncties");
		if(gameEngine == null)
		{
			gameEngine = new SpelFuncties();
			request.getServletContext().setAttribute("SpelFuncties", gameEngine);
			
		}
		
		switch(request.getParameter("operation"))
		{
		case "spelerToevoegen":
			spelersToevoegen(request, response, gameEngine);
			break;
		case "geefKaartenInHand":
			geefKaartenInHandVanDeHuidigeSpeler(request, response, gameEngine);
			break;
		case "actieKaartenGeneren":
			genereerActieKaart(request, response, gameEngine);
			break;
		case "stopBeurt":
			gameEngine.brengAlleKaartenNaarAflegstapel();
			gameEngine.spelNogNietBeëindigd();
			gameEngine.geefHuidigeSpeler().herstelWaarden();
			gameEngine.volgendeSpeler();
			break;
		case "kaartenKopen":
			kopen(request, response, gameEngine);
			break;
		case "huidigeWaarden":
			huidigeWaarden(request,response,gameEngine);
			break;
		case "infoOphalen":
			geefInfoOverDeKaart(request,response,gameEngine);
			break;
		case "brengGeldKaartenUitHandaarSpeelVeld":
			gameEngine.brengAlleGeldkaartenUitHandNaarSpeelGebied();
			break;
		case "toonSpeelVeld":
			
			geefHuidigSpeelVeld(request, response, gameEngine);
			break;
		case "trekKaartInHand":
			gameEngine.trekKaartVanTrekStapel(5);
			break;
		case "actieUitvoeren":
			kaartSpelen(request, response, gameEngine);

		default:
			break;
		}	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().append("hello world vanuit post");
	}
}