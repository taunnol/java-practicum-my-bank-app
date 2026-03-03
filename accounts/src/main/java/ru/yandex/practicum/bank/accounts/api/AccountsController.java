package ru.yandex.practicum.bank.accounts.api;

import jakarta.validation.Valid;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.bank.accounts.api.dto.*;
import ru.yandex.practicum.bank.accounts.model.Account;
import ru.yandex.practicum.bank.accounts.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    private final AccountService service;

    public AccountsController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public AccountMeResponse me(JwtAuthenticationToken auth) {
        String login = extractLogin(auth);
        Account account = service.getOrCreate(login);
        return new AccountMeResponse(account.getName(), account.getBirthdate(), account.getBalance());
    }

    @PatchMapping("/me")
    public void editMe(@Valid @RequestBody EditAccountRequest req, JwtAuthenticationToken auth) {
        String login = extractLogin(auth);
        service.updateProfile(login, req.name(), req.birthdate());
    }

    @GetMapping("/recipients")
    public List<RecipientDto> recipients(JwtAuthenticationToken auth) {
        String login = extractLogin(auth);
        return service.recipients(login).stream()
                .map(a -> new RecipientDto(a.getLogin(), a.getName()))
                .toList();
    }

    private static String extractLogin(JwtAuthenticationToken auth) {
        if (auth == null) {
            return "testuser";
        }
        String preferred = auth.getToken().getClaimAsString("preferred_username");
        return preferred != null ? preferred : auth.getName();
    }
}