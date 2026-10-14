package com.mycorp.arithmeticcalculator.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycorp.arithmeticcalculator.repository.PasswordResetTokenRepository;
import com.mycorp.arithmeticcalculator.repository.VerificationTokenRepository;

import java.time.Instant;
import java.util.Date;

@Service
@Transactional
public class TokensPurgeTask {

	private final VerificationTokenRepository tokenRepository;

	private final PasswordResetTokenRepository passwordTokenRepository;

	public TokensPurgeTask(VerificationTokenRepository tokenRepository,
			PasswordResetTokenRepository passwordTokenRepository) {
		this.tokenRepository = tokenRepository;
		this.passwordTokenRepository = passwordTokenRepository;
	}

	@Scheduled(cron = "${purge.cron.expression}")
	public void purgeExpired() {

		Date now = Date.from(Instant.now());

		passwordTokenRepository.deleteAllExpiredSince(now);
		tokenRepository.deleteAllExpiredSince(now);
	}
}
