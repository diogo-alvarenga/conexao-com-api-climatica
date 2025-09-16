import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjetoSistemaDeInformacoesClimaticasEmTempoReal{
//javac --module-path "%PATH_TO_FX%" --add-modules org.json ProjetoSistemaDeInformacoesClimaticasEmTempoReal.java
	public static void main(String[] args){

		Scanner scanner = new Scanner(System.in);
		System.out.println("Digite o nome da cidade. ");
		String cidade = scanner.nextLine();//le cidade do teclado

		try{
			String dadosClimaticos = getDadosClimaticos(cidade);//retorna um json
			//codigo 1006 significa localizacao nao encontrada


			if(dadosClimaticos.contains("\"code\":1006")){// \"code\":1006" representa "code":1006
				System.out.println("Localização nao encontrada. Tente novamente.");
			}else{
				imprimirDadosClimaticos(dadosClimaticos);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

	}

	public static String getDadosClimaticos(String cidade) throws Exception{
		String apiKey = Files.readString( Paths.get("api-key.txt")).trim();//remove os espaços no começo e no fim do texto no arquivo

		String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
		String apiUrl = "http://api.weatherapi.com/v1/current.json?key="+ apiKey +"&q="+ formataNomeCidade;
		
		HttpRequest request = HttpRequest.newBuilder()//começa a construçao de uma nova solicitaçao http
			.uri(URI.create(apiUrl))//esse metodo define o uri da solicitaçao http
				.build(); //finaliza a construçao da solicitaçao http

		//criar objeto enviar solicitaçoes http e receber respostas http, para acessar o site
		//da weather api
		HttpClient client = HttpClient.newHttpClient();//objeto com poder de enviar solicitacoes e receber respostas

		//Agora vamos enviar requisicoes http e receber respostas http, comunicar com o site da api meteorologica
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		/*o bodyhandlers diz para o cliente como lidar com a resposta
		ele ta configurado para tratar o corpo da resposta como uma string

		passo a minha requisicao: request
		e falo como eu quero a minha resposta: string*/

		return response.body();// retorna os dados meteorologicos obtidos na api
	}

	//metodo para imprimir os dados meterologicos de forma organizada
	public static void imprimirDadosClimaticos(String dados){
		//System.out.println("Dados originais(JSON) obtitidos no site meteorologico "+dados);
		JSONObject dadosJson = new JSONObject(dados);
		JSONObject informacoesMeteorologicas = dadosJson.getJSONObject("current");//current = pede os dados ATUAIS
		
		//Extrai os dados da localizaçao
		String cidade = dadosJson.getJSONObject("location").getString("name");
		String pais = dadosJson.getJSONObject("location").getString("country");

		//Extrai os dados adicionais
		String condicaoTempo = informacoesMeteorologicas.getJSONObject("condition").getString("text");//text esta dentro de condition
		int umidade = informacoesMeteorologicas.getInt("humidity");//o que nao ocorre aqui
		float velocidadeDoVento = informacoesMeteorologicas.getFloat("wind_kph");
		float pressaoAtmosferica = informacoesMeteorologicas.getFloat("pressure_mb");
		float sensacaoTermica = informacoesMeteorologicas.getFloat("feelslike_c");
		float temperaturaAtual = informacoesMeteorologicas.getFloat("temp_c");

		//extrai data e a hora da string retornada pela api
		String dataHoraString = informacoesMeteorologicas.getString("last_updated");

		//imprime informacoes meteorologicas
		System.out.println("Informações Meterologicas para "+cidade+", "+pais);
		System.out.println("Data e Hora: "+dataHoraString);
		System.out.println("Temperatura Atual: "+temperaturaAtual+"°C");
		System.out.println("Sensação Termica: "+ sensacaoTermica+ "°C");
		System.out.println("Condição do Tempo: "+condicaoTempo);
		System.out.println("Umidade: "+ umidade + "%");
		System.out.println("Velocidade do Vento: "+ velocidadeDoVento+ " km/h");
		System.out.println("Pressão Atmosférica: "+ pressaoAtmosferica+ " mb");
	}
}