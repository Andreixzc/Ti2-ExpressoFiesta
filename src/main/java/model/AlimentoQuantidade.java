package model;

import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

public class AlimentoQuantidade {

	private Alimento alimento;
	private int quantidade;

	public AlimentoQuantidade(Alimento alimento, int quantidade) {
		this.alimento = alimento;
		this.quantidade = quantidade;
	}

	public Alimento getAlimento() {
		return alimento;
	}

	public void setAlimento(Alimento alimento) {
		this.alimento = alimento;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
	@Override
	public String toString() {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
		  String json = mapper.writeValueAsString(this);
		  return json;
		} catch (JsonProcessingException e) {
		   e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
