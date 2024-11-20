package br.com.telematica.seniorx.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import br.com.telematica.seniorx.model.ApiPendencyModel;

@Component
public class PendencyQueue {

	private static final BlockingQueue<ApiPendencyModel> cachedPendency = new LinkedBlockingQueue<>();

	public static void sendCachedAllPendency(Long idDevice, String pendencyExternal, Long pendencyInternal) {
		ApiPendencyModel apiPendency = new ApiPendencyModel(idDevice, pendencyInternal, pendencyExternal);

		boolean existingKey = cachedPendency.stream().anyMatch(pendency -> pendency.getPendencyInternal().equals(pendencyInternal));

		if (existingKey) {
			for (ApiPendencyModel pendency : cachedPendency) {
				if (pendency.getPendencyInternal().toString().equals(pendencyInternal.toString()) || pendency.getPendencyInternal().intValue() == pendencyInternal.intValue()) {
					pendency.setPendencyExternal(pendencyExternal);
					break;
				}
			}
		} else {
			try {
				cachedPendency.put(apiPendency);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}

	public static List<ApiPendencyModel> sendGetAllPendency() {
		List<ApiPendencyModel> pendencies = new ArrayList<>(cachedPendency);
		return Collections.unmodifiableList(pendencies);
	}

	public static ApiPendencyModel searchKey(String chave) {
		for (ApiPendencyModel pendency : cachedPendency) {
			if (pendency.getPendencyExternal().toString().equals(chave.toString())) {
				return pendency;
			}
		}
		return null;
	}

	public static ApiPendencyModel searchKeySeniorf(String chave) {
		for (ApiPendencyModel pendency : cachedPendency) {
			if (pendency.getPendencyExternal().toString().equals(chave.toString())) {
				for (ApiPendencyModel device : cachedPendency) {
					if (device.getPendencyInternal().intValue() == pendency.getPendencyInternal().intValue() && !device.getPendencyExternal().equals(chave)) {
						clearItem(device);
						return pendency;
					}
				}
			}
		}
		return null;
	}

	public static List<ApiPendencyModel> searchKeyDevice(Long chave) {
		List<ApiPendencyModel> listApiPendency = new ArrayList<ApiPendencyModel>();
		for (ApiPendencyModel pendency : cachedPendency) {
			if (pendency.getIdDevice().toString().equals(chave.toString()) || pendency.getIdDevice().intValue() == chave.intValue()) {
				listApiPendency.add(pendency);
			}
		}

		if (listApiPendency == null || listApiPendency.isEmpty())
			return null;

		return listApiPendency;

	}

	public static ApiPendencyModel searchKey(Long chave) {
		for (ApiPendencyModel pendency : cachedPendency) {
			if ((pendency.getPendencyInternal().toString().equals(chave.toString()) || pendency.getPendencyInternal().intValue() == chave.intValue()) && pendency.getPendencyExternal() != null) {
				return pendency;
			}
		}
		return null;
	}

	public static void clearItem(ApiPendencyModel chaveItem) {
		cachedPendency.remove(chaveItem);
	}

}
